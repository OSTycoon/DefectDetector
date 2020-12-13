package me.carlayres.defect.defect;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import me.carlayres.defect.object.Defect;
import me.carlayres.defect.object.Effort;
import me.carlayres.defect.object.ReportedDefect;
import me.carlayres.defect.object.User;
import me.carlayres.defect.repository.DefectRepository;
import me.carlayres.defect.repository.ReportedDefectRepository;
import me.carlayres.defect.repository.UserRepository;

@Controller
@RequestMapping("/")
public class Server {

    private static Logger logger = LogManager.getLogger();

    @Autowired
    DefectRepository defectRepository;

    @Autowired
    ReportedDefectRepository reportedDefectRepository;

    @Autowired
    UserRepository userRepository;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','VIEW','USER')")
    @GetMapping("/")
    public String indexTable(Model model) {
        model.addAttribute("reportedDefects",
                reportedDefectRepository.findAll(Sort.by(Sort.Direction.ASC, "milepost")));
        model.addAttribute("defects", defectRepository.findAll());
        return "index.html";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','VIEW','USER')")
    @GetMapping("/breakdown")
    public String breakdownPage(Model model) {
        HashMap<String,Integer> defectCount = new HashMap<>();
        List<ReportedDefect> rds = reportedDefectRepository.findAll();
        rds.removeIf(rd -> rd.getDateRepaired() != null);
        for (ReportedDefect rd : rds)
        {
            String defectDesc = rd.getDefect().getDescription();
            Integer count = defectCount.containsKey(defectDesc) ? defectCount.get(defectDesc) : 0;
            defectCount.put(defectDesc, count + 1);
        }
        LinkedHashMap<String, Integer> sortedDefectMap = defectCount.entrySet().stream()
            .sorted(Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
                              (e1, e2) -> e1, LinkedHashMap::new));

        model.addAttribute("defectCount",
                sortedDefectMap);

        HashMap<Integer, Integer> milepostCount = new HashMap<>();
        for (ReportedDefect rd : rds)
        {
            Integer mp = (int) rd.getMilepost();
            Integer count = milepostCount.containsKey(mp) ? milepostCount.get(mp) : 0;
            milepostCount.put(mp, count + 1);
        }
        LinkedHashMap<Integer, Integer> sortedMilepostMap = milepostCount.entrySet().stream()
        .sorted(Entry.comparingByKey())
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
                          (e1, e2) -> e1, LinkedHashMap::new));

        model.addAttribute("milepostCount",
                sortedMilepostMap);

        return "breakdown.html";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','VIEW','USER')")
    @PostMapping("/index-table")
    public String indexTable(Model model, @RequestParam(name = "fromMp", required = false) String fromMpString,
            @RequestParam(name = "toMp", required = false) String toMpString,
            @RequestParam(name = "filterDefectType[]", required = false) String[] defectIds,
            @RequestParam(name = "includeRepaired", required = false) Boolean includeRepaired) {

        List<ReportedDefect> reportedDefects = new ArrayList<>();

        if (defectIds != null && defectIds.length >= 1 && !defectIds[0].isEmpty()) {
            for (String i : defectIds) {
                logger.info("defect id: " + i);
                if (fromMpString != null && !fromMpString.isEmpty() && fromMpString != null && !toMpString.isEmpty()) {
                    logger.info("mp and defects");
                    float fromMp = Float.parseFloat(fromMpString);
                    float toMp = Float.parseFloat(toMpString);
                    reportedDefects.addAll(reportedDefectRepository.findAll(Sort.by(Sort.Direction.ASC, "milepost"))
                            .stream()
                            .filter(rd -> rd.getDefect() != null && rd.getDefect().getId().equals(Long.valueOf(i)))
                            .filter(rd -> rd.getMilepost() <= toMp && rd.getMilepost() >= fromMp)
                            .collect(Collectors.toList()));
                } else {
                    logger.info("defects");
                    reportedDefects.addAll(reportedDefectRepository.findAll(Sort.by(Sort.Direction.ASC, "milepost"))
                            .stream()
                            .filter(rd -> rd.getDefect() != null && rd.getDefect().getId().equals(Long.valueOf(i)))
                            .collect(Collectors.toList()));
                }
            }
        } else {
            if (fromMpString != null && !fromMpString.isEmpty() && fromMpString != null && !toMpString.isEmpty()) {
                logger.info("mp");
                float fromMp = Float.parseFloat(fromMpString);
                float toMp = Float.parseFloat(toMpString);
                reportedDefects.addAll(reportedDefectRepository.findAll(Sort.by(Sort.Direction.ASC, "milepost"))
                        .stream().filter(rd -> rd.getMilepost() <= toMp && rd.getMilepost() >= fromMp)
                        .collect(Collectors.toList()));
            } else {
                logger.info("all");
                reportedDefects.addAll(reportedDefectRepository.findAll(Sort.by(Sort.Direction.ASC, "milepost")));
            }
        }
        if (includeRepaired == null || !includeRepaired) {
            logger.info("Don't include repaired");
            reportedDefects.removeIf(rd -> rd.getDateRepaired() != null);
        }
        Collections.sort(reportedDefects);
        model.addAttribute("reportedDefects", reportedDefects);
        model.addAttribute("defects", defectRepository.findAll());
        return "index-table.html";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/addReportedDefect")
    public String getAddReportedDefectPage(Model model) {
        model.addAttribute("defects", defectRepository.findAll());
        return "addReportedDefectPage.html";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/editDefect/{id}")
    public String getEditReportedDefectPage(@PathVariable Long id, Model model) {
        ReportedDefect rd = reportedDefectRepository.getOne(id);
        model.addAttribute("defects", defectRepository.findAll());
        model.addAttribute("milepost", rd.getMilepost());
        model.addAttribute("description", rd.getDescription());
        model.addAttribute("dateReported", rd.getDateReported());
        model.addAttribute("dateRepaired", rd.getDateRepaired());
        model.addAttribute("defectId", rd.getDefect().getId());
        model.addAttribute("rdId", rd.getId());
        return "editReportedDefectPage.html";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/editReportedDefect")
    public ResponseEntity<?> editReportedDefect(
            @RequestParam(name = "rdId") Long rdId,
            @RequestParam(name = "milepost") String milepost,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "dateReported") String dateReported,
            @RequestParam(name = "dateRepaired") String dateRepaired,
            @RequestParam(name = "defectType", required = false) String defectId) {
        if (milepost != null && !milepost.isEmpty() && description != null && !description.isEmpty()) {
            try {
                
                ReportedDefect rd = reportedDefectRepository.getOne(rdId);
                Date reportedDate = Date.valueOf(dateReported);
                if (!dateRepaired.isEmpty())
                {
                    
                    Date repairedDate = Date.valueOf(dateRepaired);
                    rd.setDateRepaired(repairedDate);
                }
                else{
                    rd.setDateRepaired(null);
                }
                rd.setMilepost(Float.parseFloat(milepost));
                rd.setDescription(description);
                rd.setDateReported(reportedDate);
                if (defectId != null && !defectId.isEmpty()) {
                    logger.info(defectId);
                    rd.setDefect(defectRepository.findById(Long.parseLong(defectId)).get());
                }
                if (!reportedDefectRepository.findAll().contains(rd)) {
                    reportedDefectRepository.save(rd);
                }
                else{
                    return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED);
                }
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (NumberFormatException ex) {
                logger.info(ex);
            }
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addReportedDefect")
    public ResponseEntity<?> addNewReportedDefect(@RequestParam(name = "milepost") String milepost,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "dateReported") String dateReported,
            @RequestParam(name = "defectType", required = false) String defectId) {
        if (milepost != null && !milepost.isEmpty() && description != null && !description.isEmpty()) {
            try {
                Date dateTime = Date.valueOf(dateReported);

                ReportedDefect rd = new ReportedDefect();
                rd.setMilepost(Float.parseFloat(milepost));
                rd.setDescription(description);
                rd.setDateReported(dateTime);
                if (defectId != null && !defectId.isEmpty()) {
                    logger.info(defectId);
                    rd.setDefect(defectRepository.findById(Long.parseLong(defectId)).get());
                }
                if (!reportedDefectRepository.findAll().contains(rd)) {
                    reportedDefectRepository.save(rd);
                }
                else{
                    return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED);
                }
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (NumberFormatException ex) {
                logger.info(ex);
            }
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/addDefect")
    public String getAddDefectPage(Model model) {
        model.addAttribute("defects", defectRepository.findAll());
        return "addDefectPage.html";
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/addUser")
    public String getAddUserPage() {
        return "addUser.html";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addDefect")
    public ResponseEntity<?> addNewDefect(@RequestParam(name = "cfr", required = false) String cfr,
            @RequestParam(name = "defectCode", required = false) String defectCode,
            @RequestParam(name = "subrule", required = false) String subrule,
            @RequestParam(name = "description") String description, @RequestParam Effort effortToRepair) {
        try {
            Defect d = new Defect();
            d.setDescription(description);
            d.setEffortToRepair(effortToRepair);
            if (cfr != null && !cfr.isEmpty()) {
                d.setCfr(cfr);
            }
            if (subrule != null && !subrule.isEmpty()) {
                d.setSubrule(subrule);
            }
            if (defectCode != null && !defectCode.isEmpty()) {
                d.setDefectCode(defectCode);
            }

            if (!defectRepository.findAll().contains(d)) {
                defectRepository.save(d);
            }
            else{
                return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NumberFormatException ex) {
            logger.info(ex);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/addUser")
    public ResponseEntity<?> addUser(@RequestParam String username,
    @RequestParam String password,
    @RequestParam String name,
    @RequestParam String auth)
    {
        User user = new User();
        user.setName(name);
        user.setGrantedAuthority(auth);
        user.setUsername(username);
        user.setPassword(passwordEncoder().encode(password));
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/markDone")
    public ResponseEntity<?> markDone(@RequestParam String id)
    {

        ReportedDefect rd = reportedDefectRepository.getOne(Long.parseLong(id));
        rd.setDateRepaired(new Date(Calendar.getInstance().getTime().getTime()));
        reportedDefectRepository.save(rd);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/deleteReportedDefect")
    public ResponseEntity<?> deleteReportedDefect(@RequestParam String id)
    {
        ReportedDefect rd = reportedDefectRepository.getOne(Long.parseLong(id));
        reportedDefectRepository.delete(rd);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

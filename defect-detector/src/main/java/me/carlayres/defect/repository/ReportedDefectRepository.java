package me.carlayres.defect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import me.carlayres.defect.object.ReportedDefect;

@Repository
public interface ReportedDefectRepository extends JpaRepository<ReportedDefect, Long>{
    
}

package me.carlayres.defect.object;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "defects")
@EntityListeners(AuditingEntityListener.class)
@EnableAutoConfiguration
public class Defect implements Serializable {

    /**
    *
    */
    static final long serialVersionUID = 7532507012453665954L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "defect_id")
    Long id;
    String cfr;
    String defectCode;
    String subrule;
    @Column(length = 1024)
    String description;
    @Enumerated(EnumType.STRING)
    Effort effortToRepair;

    public Defect() {
    }

    public Long getId() {
        return id;
    }

    public String getCfr() {
        return cfr;
    }

    public void setCfr(String cfr) {
        this.cfr = cfr;
    }

    public String getDefectCode() {
        return defectCode;
    }

    public void setDefectCode(String defectCode) {
        this.defectCode = defectCode;
    }

    public String getSubrule() {
        return subrule;
    }

    public void setSubrule(String subrule) {
        this.subrule = subrule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cfr == null) ? 0 : cfr.hashCode());
        result = prime * result + ((defectCode == null) ? 0 : defectCode.hashCode());
        result = prime * result + ((subrule == null) ? 0 : subrule.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Defect other = (Defect) obj;
        if (cfr == null) {
            if (other.cfr != null)
                return false;
        } else if (!cfr.equals(other.cfr))
            return false;
        if (defectCode == null) {
            if (other.defectCode != null)
                return false;
        } else if (!defectCode.equals(other.defectCode))
            return false;
        if (subrule == null) {
            if (other.subrule != null)
                return false;
        } else if (!subrule.equals(other.subrule))
            return false;
        return true;
    }

    public Effort getEffortToRepair() {
        return effortToRepair;
    }

    public void setEffortToRepair(Effort effortToRepair) {
        this.effortToRepair = effortToRepair;
    }


}

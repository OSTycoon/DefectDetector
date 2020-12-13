package me.carlayres.defect.object;

import java.io.Serializable;
import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "reportedDefects")
@EntityListeners(AuditingEntityListener.class)
@EnableAutoConfiguration
public class ReportedDefect implements Serializable,Comparable {

    /**
    *
    */
    static final long serialVersionUID = -3636967411131073123L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reported_defect_id")
    Long id;
    float milepost;
    String milepostPrefix;
    String milepostSuffix;
    Date dateReported;
    Date dateRepaired;
    String trackNumber;
    float latitude;
    float longitude;
    @Column(length = 1024)
    String description;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "defect_id")
    Defect defect;


    public ReportedDefect() {
    }


    public Long getId() {
        return id;
    }


    public float getMilepost() {
        return milepost;
    }


    public void setMilepost(final float milepost) {
        this.milepost = milepost;
    }


    public String getMilepostPrefix() {
        return milepostPrefix;
    }


    public void setMilepostPrefix(final String milepostPrefix) {
        this.milepostPrefix = milepostPrefix;
    }


    public String getMilepostSuffix() {
        return milepostSuffix;
    }


    public void setMilepostSuffix(final String milepostSuffix) {
        this.milepostSuffix = milepostSuffix;
    }


    public Date getDateReported() {
        return dateReported;
    }


    public void setDateReported(final Date dateReported) {
        this.dateReported = dateReported;
    }


    public Date getDateRepaired() {
        return dateRepaired;
    }


    public void setDateRepaired(final Date dateRepaired) {
        this.dateRepaired = dateRepaired;
    }


    public String getTrackNumber() {
        return trackNumber;
    }


    public void setTrackNumber(final String trackNumber) {
        this.trackNumber = trackNumber;
    }


    public float getLatitude() {
        return latitude;
    }


    public void setLatitude(final float latitude) {
        this.latitude = latitude;
    }


    public float getLongitude() {
        return longitude;
    }


    public void setLongitude(final float longitude) {
        this.longitude = longitude;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(final String description) {
        this.description = description;
    }


    public Defect getDefect() {
        return defect;
    }


    public void setDefect(final Defect defect) {
        this.defect = defect;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((defect == null) ? 0 : defect.hashCode());
        result = prime * result + Float.floatToIntBits(milepost);
        return result;
    }


    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ReportedDefect other = (ReportedDefect) obj;
        if (defect == null) {
            if (other.defect != null)
                return false;
        } else if (!defect.equals(other.defect))
            return false;
        if (Float.floatToIntBits(milepost) != Float.floatToIntBits(other.milepost))
            return false;
        return true;
    }
    @Override
    public int compareTo(Object otherO)
    {
        ReportedDefect other = (ReportedDefect) otherO;
        if (other.getMilepost() > this.getMilepost()) return -1;
        if (other.getMilepost() < this.getMilepost()) return 1;
        return 0;
    }
}

package me.carlayres.defect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import me.carlayres.defect.object.Defect;

@Repository
public interface DefectRepository extends JpaRepository<Defect, Long>{
    
}

package subject;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Subject_table")
public class Subject {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String className;
    private Long maximumStudent;

    @PostPersist
    public void onPostPersist(){
        ClassRegistered classRegistered = new ClassRegistered();
        BeanUtils.copyProperties(this, classRegistered);
        classRegistered.publishAfterCommit();


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
    public Long getMaximumStudent() {
        return maximumStudent;
    }

    public void setMaximumStudent(Long maximumStudent) {
        this.maximumStudent = maximumStudent;
    }




}

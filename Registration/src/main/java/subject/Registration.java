package subject;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Registration_table")
public class Registration {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long studentId;
    private String studentName;
    private String status;
    private Long classId;

    @PostPersist
    public void onPostPersist(){
        System.out.println("##### Registration - onPostPersist [[[   IN   ]]]");
        if(this.getStatus().equals("Register Confirmed")) {
            //##   Completed   ##
            RegisterCompleted registerCompleted = new RegisterCompleted();
            BeanUtils.copyProperties(this, registerCompleted);

            System.out.println("##### Registration(Completed) - listener  : " + registerCompleted.toJson());

            registerCompleted.publishAfterCommit();
        }
        else{
            //##   Cancelled   ##
            RegisterCancelled registerCancelled = new RegisterCancelled();
            BeanUtils.copyProperties(this, registerCancelled);

            System.out.println("##### Registration(Cancelled) - listener  : " + registerCancelled.toJson());

            registerCancelled.publishAfterCommit();
        }
    }

    @PostUpdate
    public void onPostPersistPaymentCanceled(){
        RegisterCancelled registerCancelled = new RegisterCancelled();
        BeanUtils.copyProperties(this, registerCancelled);

        System.out.println("##### Registration(Cancelled) - listener  : " + registerCancelled.toJson());

        registerCancelled.publishAfterCommit();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }




}

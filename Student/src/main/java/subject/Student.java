package subject;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Student_table")
public class Student {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String studentName;
    private String status;
    private Long classId;

    @PostPersist
    public void onPostPersist(){
        StudentRegistered studentRegistered = new StudentRegistered();
        BeanUtils.copyProperties(this, studentRegistered);
        studentRegistered.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        System.out.println("##### Student - onPostPersist #####");
        System.out.println("##### student.ID : " + this.getId());
        System.out.println("##### student.Name : " + this.getStudentName());
        System.out.println("##### student.classID : " + this.getClassId());
        System.out.println("##############################################################");

        subject.external.Registration registration = new subject.external.Registration();
        // mappings goes here
        registration.setStudentId(this.getId());  //orderId 추가
        registration.setStudentName(this.getStudentName());
        registration.setClassId(this.getClassId());
        registration.setStatus("Register Confirmed");
        StudentApplication.applicationContext.getBean(subject.external.RegistrationService.class)
            .registerRequest(registration);


    }

    @PostUpdate
    public void onPostUpdate(){
        if(this.getStatus().equals("Registration Cancelled")){
            Cancelled cancelled = new Cancelled();
            BeanUtils.copyProperties(this, cancelled);
            cancelled.publishAfterCommit();
        }
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

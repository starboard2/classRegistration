package subject;

public class ClassRegistered extends AbstractEvent {

    private Long id;
    private String className;

    public ClassRegistered(){
        super();
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
}
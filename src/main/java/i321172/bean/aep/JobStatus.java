package i321172.bean.aep;

public enum JobStatus
{
    Started, Initializing, InProgress("In Progress"), Completed;
    // donot want below to show
    // Failed, CreationFailed("Creation Failed"), Aborting, Aborted;
    public String status;

    JobStatus()
    {

    }

    JobStatus(String status)
    {
        this.status = status;
    }

    public String toStatusString()
    {
        return status != null ? status : toString();
    }
}

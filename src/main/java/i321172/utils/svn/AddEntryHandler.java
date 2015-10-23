package i321172.utils.svn;

import java.text.ParseException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

public class AddEntryHandler extends BasicEntryHandler
{
    private String author;

    public AddEntryHandler()
    {

    }

    public AddEntryHandler(String author, String start) throws ParseException
    {
        super(start);
        this.setAuthor(author);
    }

    protected boolean logEntryCondition(SVNLogEntry logEntry)
    {
        return logEntry.getAuthor().equalsIgnoreCase(getAuthor());
    }

    protected boolean checkInFileCondition(SVNLogEntryPath entry)
    {
        return super.checkInFileCondition(entry) && entry.getType() == 'A' && entry.getCopyPath() == null;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

}

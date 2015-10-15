package i321172.bean;

public class CoverageBean
{
    private boolean packag;
    private String  feature;
    private String  packageName;
    private String  className;
    private String  newTotalCoverage;
    private String  oldTotalCoverage;
    private String  coverageDiffer;
    private String  newTotalLines;
    private String  oldTotalLines;
    private String  totallinesdiffer;
    private String  newTotalLinesExecuted;
    private String  oldTotalLinesExecuted;
    private String  toBeCovered;
    private String  name;

    public boolean isPackag()
    {
        return packag;
    }

    public void verifyIsPackag()
    {
        packag = className == null || className.equals("");
        if (packag)
        {
            name = packageName;
        } else
        {
            name = className.substring(className.lastIndexOf(".") + 1);
        }
    }

    /**
     * @return className without package or packName
     */
    public String getName()
    {
        return name;
    }

    public void setPackag(boolean packag)
    {
        this.packag = packag;
    }

    public String getFeature()
    {
        return feature;
    }

    public void setFeature(String feature)
    {
        this.feature = feature;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getNewTotalCoverage()
    {
        return newTotalCoverage;
    }

    public void setNewTotalCoverage(String newTotalCoverage)
    {
        this.newTotalCoverage = newTotalCoverage;
    }

    public String getOldTotalCoverage()
    {
        return oldTotalCoverage;
    }

    public void setOldTotalCoverage(String oldTotalCoverage)
    {
        this.oldTotalCoverage = oldTotalCoverage;
    }

    public String getCoverageDiffer()
    {
        return coverageDiffer;
    }

    public void setCoverageDiffer(String coverageDiffer)
    {
        this.coverageDiffer = coverageDiffer;
    }

    public String getNewTotalLines()
    {
        return newTotalLines;
    }

    public void setNewTotalLines(String newTotalLines)
    {
        this.newTotalLines = newTotalLines;
    }

    public String getOldTotalLines()
    {
        return oldTotalLines;
    }

    public void setOldTotalLines(String oldTotalLines)
    {
        this.oldTotalLines = oldTotalLines;
    }

    public String getTotallinesdiffer()
    {
        return totallinesdiffer;
    }

    public void setTotallinesdiffer(String totallinesdiffer)
    {
        this.totallinesdiffer = totallinesdiffer;
    }

    public String getNewTotalLinesExecuted()
    {
        return newTotalLinesExecuted;
    }

    public void setNewTotalLinesExecuted(String newTotalLinesExecuted)
    {
        this.newTotalLinesExecuted = newTotalLinesExecuted;
    }

    public String getOldTotalLinesExecuted()
    {
        return oldTotalLinesExecuted;
    }

    public void setOldTotalLinesExecuted(String oldTotalLinesExecuted)
    {
        this.oldTotalLinesExecuted = oldTotalLinesExecuted;
    }

    public String getToBeCovered()
    {
        return toBeCovered;
    }

    public void setToBeCovered(String toBeCovered)
    {
        this.toBeCovered = toBeCovered;
    }

}

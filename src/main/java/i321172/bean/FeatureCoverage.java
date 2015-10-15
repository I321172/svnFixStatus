package i321172.bean;

import java.util.ArrayList;
import java.util.List;

public class FeatureCoverage
{
    private List<CoverageBean> list            = new ArrayList<CoverageBean>();
    private List<CoverageBean> onlyPackagelist = new ArrayList<CoverageBean>();

    public List<CoverageBean> getList()
    {
        return list;
    }

    public void setList(List<CoverageBean> list)
    {
        this.list = list;
    }

    public void setPackageFlag(boolean isPackage)
    {
        for (CoverageBean bean : list)
        {
            bean.setPackag(isPackage);
        }
    }

    public List<CoverageBean> getOnlyPackagelist()
    {
        if (onlyPackagelist.isEmpty())
        {
            handleOnlyPackagelist();
        }
        return onlyPackagelist;
    }

    private void handleOnlyPackagelist()
    {
        for (CoverageBean covBean : list)
        {
            if (covBean.isPackag())
            {
                onlyPackagelist.add(covBean);
            }
        }
    }

    public void setOnlyPackagelist(List<CoverageBean> list)
    {
        onlyPackagelist = list;
    }

}

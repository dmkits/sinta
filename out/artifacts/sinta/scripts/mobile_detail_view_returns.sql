DECLARE @BDATE datetime, @EDATE datetime, @StocksList VARCHAR(200)

SELECT @BDATE=?, @EDATE=?, @StocksList=?

select pc.PCatName as LABEL, sum(rd.SumCC_wt) as value
from t_CRRetD rd
INNER JOIN t_CRRet r ON r.ChID=rd.ChID
INNER JOIN r_Prods p ON p.ProdID=rd.ProdID
INNER JOIN r_ProdC pc on pc.PCatID=p.PCatID
WHERE r.DocDate BETWEEN @BDATE AND @EDATE AND @StocksList like '% '+CAST(r.StockID as varchar(200))+' %'
GROUP BY pc.PCatName
ORDER BY value DESC
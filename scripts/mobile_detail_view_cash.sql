DECLARE @BDATE datetime, @EDATE datetime, @StocksList VARCHAR(200)

SELECT @BDATE=?, @EDATE=?, @StocksList=?


SELECT 'Продажи нал' as LABEL, SUM(pays.SumCC_wt) AS VALUE
FROM t_SalePays pays
INNER JOIN t_Sales sales ON sales.ChID=pays.ChID
WHERE pays.PayformCode=1
AND sales.DocDate BETWEEN  @BDATE  AND @EDATE
AND @StocksList like '% '+CAST(sales.StockID as varchar(200))+' %'

UNION ALL
SELECT 'Возвраты нал' as LABEL, sum(pays.SumCC_wt) AS VALUE
FROM t_CRRetPays pays
INNER JOIN t_CRRet returns ON returns.ChID=pays.ChID
WHERE pays.PayformCode=1
AND returns.DocDate BETWEEN  @BDATE  AND @EDATE
AND @StocksList like '% '+CAST(returns.StockID as varchar(200))+' %'

UNION ALL
SELECT 'Вносы нал' as LABEL, sum(SumCC)AS VALUE
FROM t_monIntRec r
INNER JOIN r_Crs cr ON cr.CRID = r.CRID
WHERE r.DocDate BETWEEN @BDATE AND @EDATE
AND @StocksList like '% '+CAST(cr.StockID as varchar(200))+' %'

UNION ALL
SELECT 'Выносы нал' as LABEL,sum(SumCC)AS VALUE
FROM t_monIntExp e
INNER JOIN r_Crs cr ON cr.CRID = e.CRID
WHERE e.DocDate BETWEEN @BDATE AND @EDATE
AND @StocksList like '% '+CAST(cr.StockID as varchar(200))+' %'


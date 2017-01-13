DECLARE @BDATE datetime, @EDATE datetime, @StocksList VARCHAR(200)

SELECT @BDATE=?, @EDATE=?, @StocksList=?


select 'sales'+CAST(st.StockID as varchar(200)) as ID, st.StockName, st.StockID  AS UNIT_ID,
    REPLACE(st.StockName,'Магазин IN UA','Реализация ') as LABEL, COALESCE(SUM(TSumCC_wt),0) as VALUE_SUM,
    'sales' as DETAIL_ID
    FROM r_Stocks st
LEFT JOIN t_Sale s ON s.StockID=st.StockID AND DocDate BETWEEN  @BDATE AND @EDATE
WHERE @StocksList like '% '+CAST(st.StockID as varchar(200))+' %'
group by st.StockID, st.StockName


UNION ALL
select 'returns'+CAST(st.StockID as varchar(200)) as ID, st.StockName,st.StockID  AS UNIT_ID,
    REPLACE(st.StockName,'Магазин IN UA','Возвраты ') as LABEL, COALESCE(SUM(TSumCC_wt),0) as VALUE_SUM,
    'returns' as DETAIL_ID
FROM r_Stocks st
LEFT JOIN t_CRRet r ON r.StockID=st.StockID AND DocDate BETWEEN @BDATE AND @EDATE
WHERE @StocksList like '% '+CAST(st.StockID as varchar(200))+' %'
group by st.StockID, st.StockName


UNION ALL
  SELECT 'cash_income'+CAST(st.StockID as varchar(200)) as ID, st.StockName, st.StockID AS UNIT_ID,
    REPLACE(st.StockName,'Магазин IN UA ','Выручка НАЛ ') as LABEL, SUM(m.SumCC_wt) as VALUE_SUM,
    'cash' as DETAIL_ID
  FROM r_Stocks st
  LEFT JOIN (
     SELECT sales.StockID, COALESCE(SUM(pays.SumCC_wt), 0) as SumCC_wt
     FROM t_SalePays pays
     INNER JOIN t_Sales sales ON sales.ChID = pays.ChID
WHERE @StocksList like '% '+CAST(sales.StockID as varchar(200))+' %'
       AND pays.PayformCode = 1 AND sales.DocDate BETWEEN  @BDATE AND @EDATE
     GROUP BY sales.StockID
     UNION ALL
     SELECT crr.StockID, COALESCE(SUM(-crpays.SumCC_wt), 0) as SumCC_wt
     FROM t_CRRetPays crpays
     INNER JOIN t_CRRet crr ON crr.ChID = crpays.ChID
WHERE @StocksList like '% '+CAST(crr.StockID as varchar(200))+' %'
      AND crpays.PayformCode =1 AND crr.DocDate BETWEEN  @BDATE AND @EDATE
  GROUP BY crr.StockID
 ) m on    m.StockID = st.StockID
WHERE @StocksList like '% '+CAST(st.StockID as varchar(200))+' %'
GROUP BY st.StockID, st.StockName


UNION ALL
SELECT 'card_income'+CAST(st.StockID as varchar(200)) as ID, st.StockName,  st.StockID AS UNIT_ID,
  REPLACE(st.StockName,'Магазин IN UA ','Выручка ПК ') as LABEL, SUM(m.SumCC_wt) as VALUE_SUM,
   'card' as DETAIL_ID
FROM r_Stocks st
LEFT JOIN (
        SELECT sales.StockID, COALESCE(SUM(pays.SumCC_wt), 0) as SumCC_wt
        FROM t_SalePays pays
            INNER JOIN t_Sales sales ON sales.ChID = pays.ChID
               WHERE @StocksList like '% '+CAST(sales.StockID as varchar(200))+' %'
               AND pays.PayformCode = 2 AND sales.DocDate BETWEEN  @BDATE AND @EDATE
            GROUP BY sales.StockID
        UNION ALL
        SELECT crr.StockID, COALESCE(SUM(-crpays.SumCC_wt), 0) as SumCC_wt
        FROM t_CRRetPays crpays
            INNER JOIN t_CRRet crr ON crr.ChID = crpays.ChID
                WHERE @StocksList like '% '+CAST(crr.StockID as varchar(200))+' %'
                AND crpays.PayformCode =2 AND crr.DocDate BETWEEN  @BDATE AND @EDATE
        GROUP BY crr.StockID
) m on    m.StockID = st.StockID
 WHERE @StocksList like '% '+CAST(st.StockID as varchar(200))+' %'
GROUP BY st.StockID, st.StockName


UNION ALL
SELECT 'other_income'+CAST(st.StockID as varchar(200)) as ID, st.StockName,  st.StockID AS UNIT_ID,
REPLACE(st.StockName,'Магазин IN UA ','Выручка прочее ') as LABEL, SUM(m.SumCC_wt) as VALUE_SUM,
 'other' as DETAIL_ID
FROM r_Stocks st
LEFT JOIN (
SELECT sales.StockID, COALESCE(SUM(pays.SumCC_wt), 0) as SumCC_wt
FROM t_SalePays pays
INNER JOIN t_Sales sales ON sales.ChID = pays.ChID
 WHERE @StocksList like '% '+CAST(sales.StockID as varchar(200))+' %'
AND NOT pays.PayformCode in (1,2) AND sales.DocDate BETWEEN  @BDATE AND @EDATE
GROUP BY sales.StockID
UNION ALL
SELECT crr.StockID, COALESCE(SUM(-crpays.SumCC_wt), 0) as SumCC_wt
FROM t_CRRetPays crpays
INNER JOIN t_CRRet crr ON crr.ChID = crpays.ChID
 WHERE @StocksList like '% '+CAST(crr.StockID as varchar(200))+' %'
AND NOT crpays.PayformCode in (1,2) AND crr.DocDate BETWEEN  @BDATE AND @EDATE
 GROUP BY crr.StockID
) m on    m.StockID = st.StockID
  WHERE @StocksList like '% '+CAST(st.StockID as varchar(200))+' %'
 GROUP BY st.StockID, st.StockName
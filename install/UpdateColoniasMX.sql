UPDATE C_Suburb SET C_Municipality_ID = m.C_Municipality_ID
FROM C_Municipality m WHERE TRIM(UNACCENT(UPPER(m.Name))) LIKE TRIM(UNACCENT(UPPER(C_Suburb.Description)))
AND C_Suburb.C_Municipality_ID IS NULL;
SET @creatorId = 1;
SET @i := 0;

INSERT INTO am_role (
  created_on,
  modified_on,
  name,
  created_by
)
SELECT
  NOW(),                                   -- created_on
  NOW(),                                   -- modified_on
  CONCAT('Seed Role ', LPAD(@i := @i + 1, 3, '0')), -- name
  @creatorId                               -- created_by (must exist in am_account)
FROM (
  SELECT 1 FROM
    (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d1
  CROSS JOIN
    (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d2
) AS expanded
LIMIT 100;

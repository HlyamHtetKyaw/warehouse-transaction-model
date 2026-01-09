SET @roleId      = 1;  -- existing role_id
SET @superiorId  = 1;  -- existing superior_id (e.g., root)
SET @pwd         = '$2a$10$J4feTOnMWnwQ5sYW/QTaxOCBAnfYHVFYNjKylYcpokdSW643p9BgO'; -- existing hash
SET @i := 0;
INSERT INTO am_account (
  contact_mobile,
  contact_mobile_country_code,
  created_on,
  device_limit,
  encrypted_password,
  first_activation,
  full_name,
  login_failed,
  mail,
  mfa,
  modified_on,
  remarks,
  status,
  role_id,
  superior_id
)
SELECT
  NULL,                       -- contact_mobile
  NULL,                       -- contact_mobile_country_code
  NOW(),                      -- created_on
  1,                          -- device_limit
  @pwd,                       -- encrypted_password
  0,                          -- first_activation
  CONCAT('Test User ', LPAD(@i := @i + 1, 3, '0')), -- full_name
  0,                          -- login_failed
  CONCAT('test', LPAD(@i, 3, '0'), '@example.com'), -- mail (plain; encrypt if required)
  0,                          -- mfa
  NOW(),                      -- modified_on
  'seeded for pagination test', -- remarks
  1,                          -- status
  @roleId,                    -- role_id
  @superiorId                 -- superior_id
FROM (
  SELECT 1 FROM
    (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d1
  CROSS JOIN
    (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
     UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d2
) AS expanded
LIMIT 100;

-- Configure targets
SET @licenseTypeId = 1;   -- REQUIRED: existing license_type_id
SET @assigneeId   = NULL; -- Optional: set to existing account.account_id or leave NULL
SET @row := 0;

INSERT INTO lm_license (
    end_user_total_amount,
    end_user_left_amount,
    license_used_start_date,
    license_used_expiry_date,
    license_key,
    license_name,
    created_on,
    modified_on,
    assigner_id,
    assignee_id,
    license_type_id,
    url
)
SELECT
    100,                           -- end_user_total_amount
    100,                           -- end_user_left_amount
    NULL,                          -- license_used_start_date
    NULL,                          -- license_used_expiry_date
    CONCAT('TEST-LIC-', LPAD(@row := @row + 1, 3, '0'), '-', REPLACE(UUID(), '-', '')) AS license_key,
    CONCAT('Test License ', LPAD(@row, 3, '0')) AS license_name,
    NOW(),                         -- created_on
    NOW(),                         -- modified_on
    NULL,                          -- assigner_id
    @assigneeId,                   -- assignee_id
    @licenseTypeId,                -- license_type_id
    'https://download.example.com/installer' AS url
FROM (
    SELECT 1
    FROM (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
          UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d1
    CROSS JOIN
         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
          UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) d2
) AS expanded
LIMIT 100;

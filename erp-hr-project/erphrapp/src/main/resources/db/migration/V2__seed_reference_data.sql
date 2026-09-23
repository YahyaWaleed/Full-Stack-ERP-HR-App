/* =====================================================================
   V2  --  reference data every environment needs
   (tax brackets, payroll settings, organisation, salary components,
   leave types) plus the first administrator login.

   The admin_password_hash Flyway placeholder is filled from the
   ADMIN_PASSWORD_HASH environment variable (a BCrypt hash, never a
   plain password) -- see the README.
   ===================================================================== */

SET NAMES utf8mb4;

INSERT INTO hr_users (username, password, role)
VALUES ('admin', '${admin_password_hash}', 'HR_ADMIN');


-- ---- 9.1 payroll settings & tax brackets -----------------------------
INSERT INTO payroll_settings
 (fiscal_year, personal_exemption, ins_min_wage, ins_max_wage,
  ins_employee_rate, ins_employer_rate, working_days_month, daily_hours, overtime_factor)
VALUES
 (2025, 20000.00, 2300.00, 14500.00, 11.00, 18.75, 30, 8.0, 1.50),
 (2026, 20000.00, 2600.00, 16000.00, 11.00, 18.75, 30, 8.0, 1.50);

INSERT INTO tax_brackets (fiscal_year, from_amount, to_amount, rate) VALUES
 (2026,      0.00,   40000.00,  0.00),
 (2026,  40000.00,   55000.00, 10.00),
 (2026,  55000.00,   70000.00, 15.00),
 (2026,  70000.00,  200000.00, 20.00),
 (2026, 200000.00,  400000.00, 22.50),
 (2026, 400000.00, 99999999.00, 25.00),
 (2025,      0.00,   40000.00,  0.00),
 (2025,  40000.00,   55000.00, 10.00),
 (2025,  55000.00,   70000.00, 15.00),
 (2025,  70000.00,  200000.00, 20.00),
 (2025, 200000.00,  400000.00, 22.50),
 (2025, 400000.00, 99999999.00, 25.00);

-- ---- 9.2 branches & departments --------------------------------------
INSERT INTO branches (branch_id, code, name_en, name_ar, city, address) VALUES
 (1,'HQ',  'Head Office',        'المركز الرئيسي',   'Cairo',      '12 Street 90, New Cairo'),
 (2,'ALX', 'Alexandria Plant',   'مصنع الإسكندرية',  'Alexandria', 'Industrial Zone, Borg El Arab'),
 (3,'GIZ', 'Giza Warehouse',     'مخازن الجيزة',     'Giza',       '6th of October Industrial Area');

INSERT INTO departments (dept_id, code, name_en, name_ar, parent_dept_id, cost_center, branch_id) VALUES
 (1,'EXEC','Executive Management','الإدارة العليا',        NULL,'CC-1000',1),
 (2,'FIN', 'Finance & Accounting','المالية والحسابات',      1,   'CC-2000',1),
 (3,'HR',  'Human Resources',     'الموارد البشرية',        1,   'CC-3000',1),
 (4,'IT',  'Information Technology','تكنولوجيا المعلومات',  1,   'CC-4000',1),
 (5,'SLS', 'Sales',               'المبيعات',               1,   'CC-5000',1),
 (6,'MKT', 'Marketing',           'التسويق',                5,   'CC-5100',1),
 (7,'OPS', 'Production & Operations','الإنتاج والعمليات',   1,   'CC-6000',2),
 (8,'PRC', 'Procurement',         'المشتريات',              2,   'CC-7000',1),
 (9,'WHS', 'Warehousing & Logistics','المخازن واللوجستيات', 7,   'CC-8000',3),
 (10,'QA', 'Quality Assurance',   'ضمان الجودة',            7,   'CC-9000',2);

-- ---- 9.3 job titles ---------------------------------------------------
INSERT INTO job_titles (job_id, code, title_en, title_ar, job_grade, min_salary, max_salary, is_managerial) VALUES
 (1,'CEO',     'Chief Executive Officer','الرئيس التنفيذي',        'G7',70000,120000,1),
 (2,'CFO',     'Chief Financial Officer','المدير المالي',          'G7',55000, 90000,1),
 (3,'DIR-HR',  'HR Director',            'مدير الموارد البشرية',   'G6',40000, 65000,1),
 (4,'DIR-IT',  'IT Director',            'مدير تكنولوجيا المعلومات','G6',42000, 70000,1),
 (5,'DIR-SLS', 'Sales Director',         'مدير المبيعات',          'G6',40000, 68000,1),
 (6,'DIR-OPS', 'Operations Director',    'مدير العمليات',          'G6',40000, 66000,1),
 (7,'MGR-FIN', 'Finance Manager',        'مدير مالي',              'G5',28000, 45000,1),
 (8,'MGR-HR',  'HR Manager',             'مدير موارد بشرية',       'G5',25000, 42000,1),
 (9,'MGR-IT',  'IT Manager',             'مدير نظم المعلومات',     'G5',28000, 46000,1),
 (10,'MGR-SLS','Sales Manager',          'مدير مبيعات',            'G5',26000, 44000,1),
 (11,'MGR-MKT','Marketing Manager',      'مدير التسويق',           'G5',26000, 44000,1),
 (12,'MGR-PRD','Production Manager',     'مدير الإنتاج',           'G5',27000, 45000,1),
 (13,'MGR-PRC','Procurement Manager',    'مدير المشتريات',         'G5',24000, 40000,1),
 (14,'MGR-WHS','Warehouse Manager',      'مدير المخازن',           'G4',18000, 32000,1),
 (15,'MGR-QA', 'QA Manager',             'مدير الجودة',            'G5',25000, 42000,1),
 (16,'ACC-SR', 'Senior Accountant',      'محاسب أول',              'G3',12000, 22000,0),
 (17,'ACC',    'Accountant',             'محاسب',                  'G2', 8000, 15000,0),
 (18,'HR-SPC', 'HR Specialist',          'أخصائي موارد بشرية',     'G2', 8000, 16000,0),
 (19,'DEV-SR', 'Senior Software Engineer','مهندس برمجيات أول',     'G4',20000, 38000,0),
 (20,'DEV',    'Software Engineer',      'مهندس برمجيات',          'G3',13000, 26000,0),
 (21,'SYSADM', 'System Administrator',   'مسؤول أنظمة',            'G3',12000, 24000,0),
 (22,'SLS-REP','Sales Representative',   'مندوب مبيعات',           'G2', 7000, 14000,0),
 (23,'MKT-SPC','Marketing Specialist',   'أخصائي تسويق',           'G2', 8000, 16000,0),
 (24,'ENG-PRD','Production Engineer',    'مهندس إنتاج',            'G3',12000, 24000,0),
 (25,'TECH',   'Technician',             'فني',                    'G1', 5500, 11000,0),
 (26,'QA-INSP','Quality Inspector',      'مفتش جودة',              'G2', 7000, 14000,0),
 (27,'PRC-SPC','Procurement Specialist', 'أخصائي مشتريات',         'G2', 8000, 15000,0),
 (28,'WHS-KPR','Storekeeper',            'أمين مخزن',              'G1', 5500, 11000,0),
 (29,'DRIVER', 'Driver',                 'سائق',                   'G1', 4500,  9000,0),
 (30,'ADMIN',  'Administrative Assistant','مساعد إداري',           'G1', 5000, 10000,0);

-- ---- 9.4 salary components -------------------------------------------
INSERT INTO salary_components
 (comp_id, code, name_en, name_ar, comp_type, calc_type, default_value, is_taxable, is_insurable, is_recurring, print_order) VALUES
 (1,'BASIC','Basic Salary',       'الراتب الأساسي',     'EARNING','FIXED',    0,1,1,1, 1),
 (2,'HOUS', 'Housing Allowance',  'بدل سكن',            'EARNING','PCT_BASIC',10,1,0,1, 2),
 (3,'TRAN', 'Transport Allowance','بدل انتقالات',       'EARNING','FIXED',  800,1,0,1, 3),
 (4,'MOB',  'Mobile Allowance',   'بدل موبايل',         'EARNING','FIXED',  300,1,0,1, 4),
 (5,'FOOD', 'Meal Allowance',     'بدل وجبات',          'EARNING','FIXED',  600,0,0,1, 5),
 (6,'NAT',  'Job Nature Allowance','بدل طبيعة عمل',     'EARNING','PCT_BASIC',7,1,0,1, 6),
 (7,'SHIFT','Shift Allowance',    'بدل ورديات',         'EARNING','FIXED', 1200,1,0,1, 7),
 (8,'CAR',  'Car Allowance',      'بدل سيارة',          'EARNING','FIXED', 3500,1,0,1, 8),
 (9,'COMM', 'Sales Commission',   'عمولة مبيعات',       'EARNING','FIXED',    0,1,0,1, 9),
 (10,'OT',  'Overtime',           'بدل عمل إضافي',      'EARNING','COMPUTED', 0,1,0,0,10),
 (11,'INS', 'Social Insurance',   'التأمينات الاجتماعية','DEDUCTION','COMPUTED',0,0,0,1,20),
 (12,'TAX', 'Income Tax',         'ضريبة كسب العمل',    'DEDUCTION','COMPUTED', 0,0,0,1,21),
 (13,'ABS', 'Absence Deduction',  'خصم غياب',           'DEDUCTION','COMPUTED', 0,0,0,0,22),
 (14,'LOAN','Loan Installment',   'قسط سلفة',           'DEDUCTION','COMPUTED', 0,0,0,0,23),
 (15,'PEN', 'Penalties',          'جزاءات',             'DEDUCTION','FIXED',    0,0,0,0,24),
 (16,'CLUB','Medical Fund',       'اشتراك الصندوق الطبي','DEDUCTION','FIXED', 150,0,0,1,25),
 (17,'SYND','Syndicate Fees',     'اشتراك النقابة',     'DEDUCTION','FIXED',   50,0,0,1,26);

-- ---- 9.5 leave types --------------------------------------------------
INSERT INTO leave_types
 (type_id, code, name_en, name_ar, annual_quota, is_paid, affects_balance, max_consecutive, requires_attachment, gender_restriction) VALUES
 (1,'ANN',  'Annual Leave',    'إجازة سنوية',        21,1,1,15,0,'ANY'),
 (2,'SICK', 'Sick Leave',      'إجازة مرضية',        15,1,1,30,1,'ANY'),
 (3,'CAS',  'Casual Leave',    'إجازة عارضة',         6,1,1, 2,0,'ANY'),
 (4,'UNP',  'Unpaid Leave',    'إجازة بدون أجر',      0,0,0,30,0,'ANY'),
 (5,'MAT',  'Maternity Leave', 'إجازة وضع',          90,1,0,90,1,'F'),
 (6,'HAJJ', 'Hajj Leave',      'إجازة حج',           30,1,0,30,1,'ANY'),
 (7,'MRG',  'Marriage Leave',  'إجازة زواج',          5,1,0, 5,1,'ANY'),
 (8,'BRV',  'Bereavement Leave','إجازة وفاة',         3,1,0, 3,0,'ANY');

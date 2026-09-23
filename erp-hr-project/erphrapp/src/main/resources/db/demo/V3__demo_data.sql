/* =====================================================================
   V3  --  demo data: 35 employees, contracts, leave, loans, attendance
   and four payroll runs. Lives in db/demo, which only the dev profile
   and the tests load -- production never sees it.
   ===================================================================== */

SET NAMES utf8mb4;

-- ---- 9.6 employees (35) ----------------------------------------------
-- 9.6.a Executive layer
INSERT INTO employees
 (emp_id, emp_code, full_name_ar, full_name_en, gender, birth_date, national_id, marital_status,
  dependents, email, mobile, address, hire_date, dept_id, job_id, branch_id, manager_id,
  emp_status, termination_date, insurance_no, bank_name, bank_account, payment_method) VALUES
 (1,'EMP-0001','أحمد سمير عبد العزيز','Ahmed Samir Abdelaziz','M','1972-03-14','27203141201531','MARRIED',3,'a.samir@nile-ind.com','01001234501','التجمع الخامس، القاهرة','2010-01-04',1,1,1,NULL,'ACTIVE',NULL,'INS-100001','CIB','EG380019000500000001234501','BANK'),
 (2,'EMP-0002','منى خالد إبراهيم','Mona Khaled Ibrahim','F','1978-07-22','27807221202542','MARRIED',2,'m.khaled@nile-ind.com','01001234502','مدينة نصر، القاهرة','2011-05-15',2,2,1,1,'ACTIVE',NULL,'INS-100002','CIB','EG380019000500000001234502','BANK'),
 (3,'EMP-0003','طارق محمود سعيد','Tarek Mahmoud Saeed','M','1980-11-09','28011091203553','MARRIED',2,'t.saeed@nile-ind.com','01001234503','المعادي، القاهرة','2013-02-01',3,3,1,1,'ACTIVE',NULL,'INS-100003','CIB','EG380019000500000001234503','BANK'),
 (4,'EMP-0004','هالة عصام فؤاد','Hala Essam Fouad','F','1983-04-18','28304181204564','MARRIED',1,'h.essam@nile-ind.com','01001234504','الشيخ زايد، الجيزة','2014-08-10',4,4,1,1,'ACTIVE',NULL,'INS-100004','QNB','EG380019000500000001234504','BANK'),
 (5,'EMP-0005','عمرو ياسر الشناوي','Amr Yasser Elshennawy','M','1981-09-05','28109051205575','MARRIED',3,'a.yasser@nile-ind.com','01001234505','مصر الجديدة، القاهرة','2012-06-01',5,5,1,1,'ACTIVE',NULL,'INS-100005','QNB','EG380019000500000001234505','BANK'),
 (6,'EMP-0006','نهى فتحي عبد الله','Noha Fathy Abdallah','F','1979-12-30','27912301206586','MARRIED',2,'n.fathy@nile-ind.com','01001234506','سموحة، الإسكندرية','2012-09-16',7,6,2,1,'ACTIVE',NULL,'INS-100006','NBE','EG380019000500000001234506','BANK');

-- 9.6.b Middle management
INSERT INTO employees
 (emp_id, emp_code, full_name_ar, full_name_en, gender, birth_date, national_id, marital_status,
  dependents, email, mobile, address, hire_date, dept_id, job_id, branch_id, manager_id,
  emp_status, termination_date, insurance_no, bank_name, bank_account, payment_method) VALUES
 (7,'EMP-0007','كريم عادل منصور','Karim Adel Mansour','M','1986-02-11','28602111207597','MARRIED',2,'k.adel@nile-ind.com','01001234507','مدينة نصر، القاهرة','2016-03-01',2,7,1,2,'ACTIVE',NULL,'INS-100007','CIB','EG380019000500000001234507','BANK'),
 (8,'EMP-0008','دينا مصطفى رشاد','Dina Mostafa Rashad','F','1988-06-25','28806251208508','MARRIED',1,'d.mostafa@nile-ind.com','01001234508','الرحاب، القاهرة','2017-07-02',3,8,1,3,'ACTIVE',NULL,'INS-100008','CIB','EG380019000500000001234508','BANK'),
 (9,'EMP-0009','محمد جمال الدين حسن','Mohamed Gamaleldin Hassan','M','1987-10-03','28710031209519','MARRIED',3,'m.gamal@nile-ind.com','01001234509','أكتوبر، الجيزة','2016-11-14',4,9,1,4,'ACTIVE',NULL,'INS-100009','QNB','EG380019000500000001234509','BANK'),
 (10,'EMP-0010','شريف نبيل حلمي','Sherif Nabil Helmy','M','1985-05-19','28505191210520','MARRIED',2,'s.nabil@nile-ind.com','01001234510','المهندسين، الجيزة','2015-04-05',5,10,1,5,'ACTIVE',NULL,'INS-100010','QNB','EG380019000500000001234510','BANK'),
 (11,'EMP-0011','إيمان رأفت زكي','Eman Raafat Zaki','F','1989-08-08','28908081211531','SINGLE',0,'e.raafat@nile-ind.com','01001234511','الدقي، الجيزة','2018-01-15',6,11,1,5,'ACTIVE',NULL,'INS-100011','CIB','EG380019000500000001234511','BANK'),
 (12,'EMP-0012','هشام عبد الرحمن عطية','Hisham Abdelrahman Attia','M','1984-01-27','28401271212542','MARRIED',4,'h.attia@nile-ind.com','01001234512','برج العرب، الإسكندرية','2015-10-01',7,12,2,6,'ACTIVE',NULL,'INS-100012','NBE','EG380019000500000001234512','BANK'),
 (13,'EMP-0013','ولاء سمير القاضي','Walaa Samir Elkady','F','1987-03-30','28703301213553','MARRIED',2,'w.samir@nile-ind.com','01001234513','حدائق الأهرام، الجيزة','2018-05-20',8,13,1,2,'ACTIVE',NULL,'INS-100013','NBE','EG380019000500000001234513','BANK'),
 (14,'EMP-0014','أيمن صبحي غانم','Ayman Sobhy Ghanem','M','1986-09-12','28609121214564','MARRIED',3,'a.sobhy@nile-ind.com','01001234514','أكتوبر، الجيزة','2017-02-06',9,14,3,6,'ACTIVE',NULL,'INS-100014','NBE','EG380019000500000001234514','BANK'),
 (15,'EMP-0015','رانيا وجدي سلامة','Rania Wagdy Salama','F','1988-11-21','28811211215575','MARRIED',1,'r.wagdy@nile-ind.com','01001234515','ميامي، الإسكندرية','2018-09-03',10,15,2,6,'ACTIVE',NULL,'INS-100015','NBE','EG380019000500000001234515','BANK');

-- 9.6.c Staff
INSERT INTO employees
 (emp_id, emp_code, full_name_ar, full_name_en, gender, birth_date, national_id, marital_status,
  dependents, email, mobile, address, hire_date, dept_id, job_id, branch_id, manager_id,
  emp_status, termination_date, insurance_no, bank_name, bank_account, payment_method) VALUES
 (16,'EMP-0016','محمود إسماعيل بدوي','Mahmoud Ismail Badawy','M','1992-04-04','29204041216586','MARRIED',1,'m.ismail@nile-ind.com','01001234516','شبرا، القاهرة','2019-03-10',2,16,1,7,'ACTIVE',NULL,'INS-100016','CIB','EG380019000500000001234516','BANK'),
 (17,'EMP-0017','سارة أشرف الديب','Sara Ashraf Eldeeb','F','1995-07-17','29507171217597','SINGLE',0,'s.ashraf@nile-ind.com','01001234517','المعادي، القاهرة','2021-06-01',2,17,1,7,'ACTIVE',NULL,'INS-100017','CIB','EG380019000500000001234517','BANK'),
 (18,'EMP-0018','عمر حسام الدين علي','Omar Hosameldin Ali','M','1996-02-23','29602231218508','SINGLE',0,'o.hosam@nile-ind.com','01001234518','حلوان، القاهرة','2022-09-11',2,17,1,7,'ACTIVE',NULL,'INS-100018','CIB','EG380019000500000001234518','BANK'),
 (19,'EMP-0019','نور الهدى ماهر','Nour Elhoda Maher','F','1994-10-08','29410081219519','MARRIED',1,'n.maher@nile-ind.com','01001234519','الزيتون، القاهرة','2020-02-17',3,18,1,8,'ACTIVE',NULL,'INS-100019','CIB','EG380019000500000001234519','BANK'),
 (20,'EMP-0020','كريم أنور شحاتة','Karim Anwar Shehata','M','1990-12-15','29012151220520','MARRIED',2,'k.anwar@nile-ind.com','01001234520','التجمع الأول، القاهرة','2019-08-25',4,19,1,9,'ACTIVE',NULL,'INS-100020','QNB','EG380019000500000001234520','BANK'),
 (21,'EMP-0021','مريم طارق الغندور','Mariam Tarek Elghandour','F','1997-05-06','29705061221531','MARRIED',1,'m.tarek@nile-ind.com','01001234521','مدينة نصر، القاهرة','2022-01-09',4,20,1,9,'ACTIVE',NULL,'INS-100021','QNB','EG380019000500000001234521','BANK'),
 (22,'EMP-0022','يوسف عماد الشريف','Youssef Emad Elsherif','M','1998-01-30','29801301222542','SINGLE',0,'y.emad@nile-ind.com','01001234522','فيصل، الجيزة','2024-03-03',4,20,1,9,'ACTIVE',NULL,'INS-100022','QNB','EG380019000500000001234522','BANK'),
 (23,'EMP-0023','خالد رمضان سويلم','Khaled Ramadan Soueilem','M','1993-06-14','29306141223553','MARRIED',2,'k.ramadan@nile-ind.com','01001234523','العبور، القليوبية','2021-11-21',4,21,1,9,'ACTIVE',NULL,'INS-100023','QNB','EG380019000500000001234523','BANK'),
 (24,'EMP-0024','أحمد فتحي الجندي','Ahmed Fathy Elgindy','M','1995-09-27','29509271224564','SINGLE',0,'a.fathy@nile-ind.com','01001234524','الهرم، الجيزة','2021-03-14',5,22,1,10,'ACTIVE',NULL,'INS-100024','QNB','EG380019000500000001234524','BANK'),
 (25,'EMP-0025','ياسمين علاء الحسيني','Yasmin Alaa Elhosseiny','F','1996-11-19','29611191225575','SINGLE',0,'y.alaa@nile-ind.com','01001234525','مصر الجديدة، القاهرة','2023-05-02',5,22,1,10,'ACTIVE',NULL,'INS-100025','QNB','EG380019000500000001234525','BANK'),
 (26,'EMP-0026','محمد سعد الطوخي','Mohamed Saad Eltoukhy','M','1994-03-08','29403081226586','MARRIED',1,'m.saad@nile-ind.com','01001234526','الدقي، الجيزة','2022-04-18',6,23,1,11,'ACTIVE',NULL,'INS-100026','CIB','EG380019000500000001234526','BANK'),
 (27,'EMP-0027','إبراهيم عبد الستار محمد','Ibrahim Abdelsattar Mohamed','M','1989-07-11','28907111227597','MARRIED',3,'i.abdelsattar@nile-ind.com','01001234527','العامرية، الإسكندرية','2019-01-07',7,24,2,12,'ACTIVE',NULL,'INS-100027','NBE','EG380019000500000001234527','BANK'),
 (28,'EMP-0028','مصطفى عادل زهران','Mostafa Adel Zahran','M','1991-02-16','29102161228508','MARRIED',2,'m.adel@nile-ind.com','01001234528','برج العرب، الإسكندرية','2020-06-15',7,25,2,12,'ACTIVE',NULL,'INS-100028','NBE','EG380019000500000001234528','BANK'),
 (29,'EMP-0029','سيد عبد المنعم بكري','Sayed Abdelmoneim Bakry','M','1985-08-29','28508291229519','MARRIED',4,'s.bakry@nile-ind.com','01001234529','أبو قير، الإسكندرية','2016-05-03',7,25,2,12,'ACTIVE',NULL,'INS-100029','NBE','EG380019000500000001234529','BANK'),
 (30,'EMP-0030','هدى ناصر الشامي','Hoda Nasser Elshamy','F','1993-12-02','29312021230520','SINGLE',0,'h.nasser@nile-ind.com','01001234530','المنتزه، الإسكندرية','2021-09-19',10,26,2,15,'ACTIVE',NULL,'INS-100030','NBE','EG380019000500000001234530','BANK'),
 (31,'EMP-0031','طلعت زكريا العايدي','Talaat Zakaria Elaydy','M','1990-05-23','29005231231531','MARRIED',2,'t.zakaria@nile-ind.com','01001234531','إمبابة، الجيزة','2020-10-11',8,27,1,13,'ACTIVE',NULL,'INS-100031','CIB','EG380019000500000001234531','BANK'),
 (32,'EMP-0032','حسن عبد الحميد فرج','Hassan Abdelhamid Farag','M','1987-06-07','28706071232542','MARRIED',3,'h.farag@nile-ind.com','01001234532','أكتوبر، الجيزة','2018-12-02',9,28,3,14,'ACTIVE',NULL,'INS-100032','NBE','EG380019000500000001234532','BANK'),
 (33,'EMP-0033','رجب سعيد المنشاوي','Ragab Saeed Elmenshawy','M','1983-10-14','28310141233553','MARRIED',5,'r.saeed@nile-ind.com','01001234533','أوسيم، الجيزة','2019-04-01',9,29,3,14,'ACTIVE',NULL,'INS-100033','NBE','EG380019000500000001234533','CASH'),
 (34,'EMP-0034','آية محمد سلطان','Aya Mohamed Sultan','F','1999-02-09','29902091234564','SINGLE',0,'a.sultan@nile-ind.com','01001234534','عين شمس، القاهرة','2026-06-15',3,30,1,8,'PROBATION',NULL,'INS-100034','CIB','EG380019000500000001234534','BANK'),
 (35,'EMP-0035','مينا نبيل فهمي','Mina Nabil Fahmy','M','1992-08-21','29208211235575','MARRIED',1,'m.nabil@nile-ind.com','01001234535','شبرا الخيمة، القليوبية','2023-02-13',5,22,1,10,'RESIGNED','2026-06-30','INS-100035','QNB','EG380019000500000001234535','BANK');

-- ---- 9.7 contracts ----------------------------------------------------
INSERT INTO employee_contracts
 (emp_id, contract_no, contract_type, start_date, end_date, basic_salary, weekly_hours,
  annual_leave_days, probation_months, status, notes) VALUES
 (1,'CT-2010-001','PERMANENT','2010-01-04',NULL,85000.00,40,30,0,'ACTIVE','عقد الرئيس التنفيذي'),
 (2,'CT-2011-002','PERMANENT','2011-05-15',NULL,62000.00,40,30,3,'ACTIVE',NULL),
 (3,'CT-2013-003','PERMANENT','2013-02-01',NULL,48000.00,40,30,3,'ACTIVE',NULL),
 (4,'CT-2014-004','PERMANENT','2014-08-10',NULL,52000.00,40,30,3,'ACTIVE',NULL),
 (5,'CT-2012-005','PERMANENT','2012-06-01',NULL,50000.00,40,30,3,'ACTIVE',NULL),
 (6,'CT-2012-006','PERMANENT','2012-09-16',NULL,47000.00,40,30,3,'ACTIVE',NULL),
 (7,'CT-2016-007','PERMANENT','2016-03-01',NULL,34000.00,40,25,3,'ACTIVE',NULL),
 (8,'CT-2017-008','PERMANENT','2017-07-02',NULL,30000.00,40,25,3,'ACTIVE',NULL),
 (9,'CT-2016-009','PERMANENT','2016-11-14',NULL,36000.00,40,25,3,'ACTIVE',NULL),
 (10,'CT-2015-010','PERMANENT','2015-04-05',NULL,32000.00,40,25,3,'ACTIVE',NULL),
 (11,'CT-2018-011','PERMANENT','2018-01-15',NULL,29000.00,40,21,3,'ACTIVE',NULL),
 (12,'CT-2015-012','PERMANENT','2015-10-01',NULL,33000.00,48,25,3,'ACTIVE',NULL),
 (13,'CT-2018-013','PERMANENT','2018-05-20',NULL,27000.00,40,21,3,'ACTIVE',NULL),
 (14,'CT-2017-014','PERMANENT','2017-02-06',NULL,22000.00,48,21,3,'ACTIVE',NULL),
 (15,'CT-2018-015','PERMANENT','2018-09-03',NULL,28000.00,40,21,3,'ACTIVE',NULL),
 (16,'CT-2019-016','PERMANENT','2019-03-10',NULL,17000.00,40,21,3,'ACTIVE',NULL),
 (17,'CT-2021-017','PERMANENT','2021-06-01',NULL,11000.00,40,21,3,'ACTIVE',NULL),
 (18,'CT-2022-018','PERMANENT','2022-09-11',NULL, 9500.00,40,21,3,'ACTIVE',NULL),
 (19,'CT-2020-019','PERMANENT','2020-02-17',NULL,12000.00,40,21,3,'ACTIVE',NULL),
 (20,'CT-2019-020','PERMANENT','2019-08-25','2022-12-31',20000.00,40,21,3,'EXPIRED','عقد سابق قبل الترقية'),
 (20,'CT-2023-020','PERMANENT','2023-01-01',NULL,30000.00,40,25,0,'ACTIVE','ترقية إلى مهندس برمجيات أول'),
 (21,'CT-2022-021','PERMANENT','2022-01-09',NULL,18000.00,40,21,3,'ACTIVE',NULL),
 (22,'CT-2024-022','FIXED_TERM','2024-03-03','2027-03-02',14000.00,40,21,3,'ACTIVE',NULL),
 (23,'CT-2021-023','PERMANENT','2021-11-21',NULL,16000.00,40,21,3,'ACTIVE',NULL),
 (24,'CT-2021-024','PERMANENT','2021-03-14',NULL, 9000.00,40,21,3,'ACTIVE',NULL),
 (25,'CT-2023-025','PERMANENT','2023-05-02',NULL, 8000.00,40,21,3,'ACTIVE',NULL),
 (26,'CT-2022-026','PERMANENT','2022-04-18',NULL,11000.00,40,21,3,'ACTIVE',NULL),
 (27,'CT-2019-027','PERMANENT','2019-01-07',NULL,18000.00,48,21,3,'ACTIVE',NULL),
 (28,'CT-2020-028','PERMANENT','2020-06-15',NULL, 8000.00,48,21,3,'ACTIVE',NULL),
 (29,'CT-2016-029','PERMANENT','2016-05-03',NULL, 9500.00,48,25,3,'ACTIVE',NULL),
 (30,'CT-2021-030','PERMANENT','2021-09-19',NULL, 9000.00,48,21,3,'ACTIVE',NULL),
 (31,'CT-2020-031','PERMANENT','2020-10-11',NULL,11500.00,40,21,3,'ACTIVE',NULL),
 (32,'CT-2018-032','PERMANENT','2018-12-02',NULL, 7500.00,48,21,3,'ACTIVE',NULL),
 (33,'CT-2019-033','PERMANENT','2019-04-01',NULL, 6000.00,48,21,3,'ACTIVE',NULL),
 (34,'CT-2026-034','FIXED_TERM','2026-06-15','2027-06-14',6500.00,40,21,3,'ACTIVE','تحت الاختبار'),
 (35,'CT-2023-035','PERMANENT','2023-02-13','2026-06-30',8500.00,40,21,3,'ACTIVE','انتهت الخدمة بالاستقالة');

-- ---- 9.8 salary components per employee (rule based) ------------------
-- Housing: 15% for grades G5+, 10% for the rest
INSERT INTO employee_salary_components (emp_id, comp_id, percentage, effective_from, notes)
SELECT e.emp_id, 2, IF(j.job_grade IN ('G5','G6','G7'), 15.00, 10.00),
       GREATEST(e.hire_date, '2024-01-01'), 'بدل سكن حسب الدرجة الوظيفية'
FROM employees e JOIN job_titles j ON j.job_id = e.job_id;

-- Transport: 1,500 for managers / 800 default
INSERT INTO employee_salary_components (emp_id, comp_id, amount, effective_from, notes)
SELECT e.emp_id, 3, IF(j.is_managerial = 1, 1500.00, NULL),
       GREATEST(e.hire_date, '2024-01-01'), 'بدل انتقالات'
FROM employees e JOIN job_titles j ON j.job_id = e.job_id;

-- Mobile allowance: grade G3 and above
INSERT INTO employee_salary_components (emp_id, comp_id, amount, effective_from, notes)
SELECT e.emp_id, 4, NULL, GREATEST(e.hire_date, '2024-01-01'), 'بدل خط موبايل'
FROM employees e JOIN job_titles j ON j.job_id = e.job_id
WHERE j.job_grade IN ('G3','G4','G5','G6','G7');

-- Meal allowance: plant & warehouse staff
INSERT INTO employee_salary_components (emp_id, comp_id, amount, effective_from, notes)
SELECT e.emp_id, 5, NULL, GREATEST(e.hire_date, '2024-01-01'), 'بدل وجبات للمواقع الصناعية'
FROM employees e WHERE e.branch_id IN (2,3);

-- Job nature allowance: production, warehousing, QA
INSERT INTO employee_salary_components (emp_id, comp_id, percentage, effective_from, notes)
SELECT e.emp_id, 6, NULL, GREATEST(e.hire_date, '2024-01-01'), 'بدل طبيعة عمل'
FROM employees e WHERE e.dept_id IN (7,9,10);

-- Shift allowance: technicians, storekeeper, driver
INSERT INTO employee_salary_components (emp_id, comp_id, amount, effective_from, notes)
SELECT e.emp_id, 7, NULL, GREATEST(e.hire_date, '2024-01-01'), 'بدل ورديات'
FROM employees e WHERE e.job_id IN (25,28,29);

-- Car allowance: managerial roles (6,000 for directors and above)
INSERT INTO employee_salary_components (emp_id, comp_id, amount, effective_from, notes)
SELECT e.emp_id, 8, IF(j.job_grade IN ('G6','G7'), 6000.00, NULL),
       GREATEST(e.hire_date, '2024-01-01'), 'بدل سيارة'
FROM employees e JOIN job_titles j ON j.job_id = e.job_id
WHERE j.is_managerial = 1;

-- Sales commission (individual targets)
INSERT INTO employee_salary_components (emp_id, comp_id, amount, effective_from, notes) VALUES
 (24, 9, 4500.00, '2025-01-01', 'عمولة حسب تحقيق التارجت'),
 (25, 9, 3800.00, '2025-01-01', 'عمولة حسب تحقيق التارجت'),
 (35, 9, 2600.00, '2025-01-01', 'عمولة حسب تحقيق التارجت'),
 (10, 9, 6000.00, '2025-01-01', 'عمولة إدارة المبيعات');

-- Medical fund: everybody / Syndicate fees: engineers only
INSERT INTO employee_salary_components (emp_id, comp_id, amount, effective_from, notes)
SELECT e.emp_id, 16, NULL, GREATEST(e.hire_date, '2024-01-01'), 'اشتراك الصندوق الطبي' FROM employees e;

INSERT INTO employee_salary_components (emp_id, comp_id, amount, effective_from, notes)
SELECT e.emp_id, 17, NULL, GREATEST(e.hire_date, '2024-01-01'), 'اشتراك نقابة المهندسين'
FROM employees e WHERE e.job_id IN (19,20,21,24);

-- One-off penalties (limited to a single payroll month)
INSERT INTO employee_salary_components (emp_id, comp_id, amount, effective_from, effective_to, notes) VALUES
 (28, 15, 200.00, '2026-07-01', '2026-07-31', 'جزاء مخالفة اشتراطات السلامة'),
 (24, 15, 300.00, '2026-05-01', '2026-05-31', 'جزاء تأخير متكرر');

-- ---- 9.9 leave balances for FY2026 ------------------------------------
INSERT INTO leave_balances (emp_id, type_id, fiscal_year, entitled_days, carried_forward, used_days)
SELECT e.emp_id, lt.type_id, 2026,
       CASE WHEN lt.code = 'ANN' THEN IFNULL(c.annual_leave_days, lt.annual_quota) ELSE lt.annual_quota END,
       CASE WHEN lt.code = 'ANN' THEN MOD(e.emp_id, 5) ELSE 0 END,
       0
FROM employees e
JOIN leave_types lt ON lt.affects_balance = 1 AND lt.gender_restriction IN ('ANY', e.gender)
LEFT JOIN employee_contracts c ON c.emp_id = e.emp_id AND c.status = 'ACTIVE'
ON DUPLICATE KEY UPDATE
  entitled_days = VALUES(entitled_days), carried_forward = VALUES(carried_forward);

-- ---- 9.10 leave requests (inserted PENDING, then decided -> triggers) --
INSERT INTO leave_requests (request_id, emp_id, type_id, start_date, end_date, days_count, reason, applied_on) VALUES
 (1, 16,1,'2026-02-09','2026-02-13', 5,'إجازة سنوية - سفر عائلي','2026-01-25'),
 (2, 17,3,'2026-03-02','2026-03-02', 1,'ظرف عائلي طارئ','2026-03-02'),
 (3, 20,1,'2026-04-06','2026-04-16',11,'إجازة سنوية','2026-03-15'),
 (4, 21,5,'2026-04-01','2026-06-29',90,'إجازة وضع','2026-03-10'),
 (5, 23,2,'2026-05-11','2026-05-14', 4,'إجازة مرضية بتقرير طبي','2026-05-11'),
 (6, 24,1,'2026-05-18','2026-05-21', 4,'إجازة سنوية','2026-05-05'),
 (7, 27,1,'2026-05-25','2026-05-28', 4,'إجازة سنوية','2026-05-10'),
 (8, 28,4,'2026-06-08','2026-06-10', 3,'إجازة بدون أجر','2026-06-01'),
 (9, 30,3,'2026-06-15','2026-06-16', 2,'إجازة عارضة','2026-06-15'),
 (10,19,1,'2026-06-21','2026-06-25', 5,'إجازة سنوية','2026-06-07'),
 (11,32,2,'2026-06-29','2026-07-02', 4,'إجازة مرضية','2026-06-29'),
 (12,31,7,'2026-07-05','2026-07-09', 5,'إجازة زواج','2026-06-20'),
 (13,26,1,'2026-07-12','2026-07-16', 5,'إجازة سنوية','2026-06-28'),
 (14,29,6,'2026-07-01','2026-07-30',30,'إجازة حج','2026-05-30'),
 (15,18,1,'2026-07-19','2026-07-23', 5,'إجازة سنوية','2026-07-05'),
 (16,33,4,'2026-07-27','2026-07-29', 3,'إجازة بدون أجر','2026-07-20'),
 (17,22,1,'2026-08-02','2026-08-06', 5,'إجازة سنوية','2026-07-19'),
 (18,25,3,'2026-08-10','2026-08-11', 2,'إجازة عارضة','2026-08-10'),
 (19,30,1,'2026-08-16','2026-08-20', 5,'إجازة سنوية','2026-08-02'),
 (20,17,1,'2026-08-23','2026-08-27', 5,'إجازة سنوية','2026-08-13'),
 (21,34,3,'2026-08-24','2026-08-25', 2,'إجازة عارضة','2026-08-20'),
 (22,23,1,'2026-09-06','2026-09-10', 5,'إجازة سنوية','2026-08-18'),
 (23,16,2,'2026-08-30','2026-09-01', 3,'إجازة مرضية','2026-08-22');

-- approvals (fire trg_leave_approved -> update balances)
UPDATE leave_requests SET status='APPROVED', approver_id = CASE
    WHEN emp_id IN (16,17,18) THEN 7  WHEN emp_id IN (19,34) THEN 8
    WHEN emp_id IN (20,21,22,23) THEN 9 WHEN emp_id IN (24,25,35) THEN 10
    WHEN emp_id = 26 THEN 11 WHEN emp_id IN (27,28,29) THEN 12
    WHEN emp_id = 31 THEN 13 WHEN emp_id IN (32,33) THEN 14
    WHEN emp_id = 30 THEN 15 ELSE 3 END,
    decided_on = DATE_ADD(applied_on, INTERVAL 1 DAY)
WHERE request_id IN (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,18,19);

UPDATE leave_requests SET status='REJECTED', approver_id = 9, decided_on = '2026-07-21',
       reject_reason = 'ذروة تسليم مشروع - يُرجى إعادة الطلب في سبتمبر'
WHERE request_id = 17;

-- requests 20..23 remain PENDING (awaiting approval)

-- ---- 9.11 loans / advances --------------------------------------------
INSERT INTO loans (loan_id, emp_id, loan_type, principal_amount, installments_count,
                   monthly_installment, remaining_balance, start_period, approved_by, status, request_date) VALUES
 (1,17,'ADVANCE',   20000.00,10, 2000.00, 16000.00,'2026-03',2,'ACTIVE','2026-02-20'),
 (2,24,'EMERGENCY', 15000.00, 6, 2500.00, 12500.00,'2026-04',2,'ACTIVE','2026-03-22'),
 (3,28,'PERSONAL',  30000.00,12, 2500.00, 20000.00,'2026-01',2,'ACTIVE','2025-12-15'),
 (4,33,'ADVANCE',    6000.00, 3, 2000.00,  6000.00,'2026-05',3,'ACTIVE','2026-04-19'),
 (5,21,'HOUSING',  100000.00,24, 4166.67, 87500.00,'2026-02',1,'ACTIVE','2026-01-11'),
 (6,16,'ADVANCE',   10000.00, 5, 2000.00,     0.00,'2025-06',2,'CLOSED','2025-05-18');

-- ---- 9.12 payroll periods ---------------------------------------------
INSERT INTO payroll_periods (period_id, period_code, fiscal_year, start_date, end_date, pay_date, working_days, status) VALUES
 (1,'2026-05',2026,'2026-05-01','2026-05-31','2026-05-28',21,'OPEN'),
 (2,'2026-06',2026,'2026-06-01','2026-06-30','2026-06-28',22,'OPEN'),
 (3,'2026-07',2026,'2026-07-01','2026-07-31','2026-07-28',23,'OPEN'),
 (4,'2026-08',2026,'2026-08-01','2026-08-31','2026-08-27',21,'OPEN');

-- ---- 9.13 monthly attendance summary ----------------------------------
INSERT INTO attendance_summary
 (period_id, emp_id, working_days, present_days, paid_leave_days, unpaid_absent_days, overtime_hours, late_minutes)
SELECT period_id, emp_id, working_days,
       working_days - paid_leave - unpaid, paid_leave, unpaid, ot, late
FROM (
  SELECT p.period_id, e.emp_id, p.working_days,
         CASE WHEN MOD(e.emp_id * 7 + p.period_id * 13, 9)  = 0 THEN 2.0
              WHEN MOD(e.emp_id * 7 + p.period_id * 13, 11) = 5 THEN 1.0
              ELSE 0 END AS paid_leave,
         CASE WHEN MOD(e.emp_id * 5 + p.period_id * 17, 19) = 0 THEN 1.0
              WHEN MOD(e.emp_id * 5 + p.period_id * 17, 23) = 7 THEN 0.5
              ELSE 0 END AS unpaid,
         CASE WHEN j.is_managerial = 0 AND j.job_grade IN ('G1','G2','G3')
              THEN MOD(e.emp_id * 3 + p.period_id * 7, 26) ELSE 0 END AS ot,
         MOD(e.emp_id * 13 + p.period_id * 29, 75) AS late
  FROM payroll_periods p
  JOIN employees e   ON e.hire_date <= p.end_date
                    AND (e.termination_date IS NULL OR e.termination_date >= p.start_date)
  JOIN job_titles j  ON j.job_id = e.job_id
) x;

/* =====================================================================
   10. RUN THE PAYROLL ENGINE
   ===================================================================== */
CALL sp_run_payroll('2026-05');
CALL sp_run_payroll('2026-06');
CALL sp_run_payroll('2026-07');
CALL sp_run_payroll('2026-08');   -- current month: processed, not paid yet

CALL sp_pay_period('2026-05','TRF');
CALL sp_pay_period('2026-06','TRF');
CALL sp_pay_period('2026-07','TRF');


UPDATE payroll_periods SET status = 'CLOSED' WHERE period_code IN ('2026-05','2026-06');
UPDATE payslips SET status = 'APPROVED'
 WHERE period_id = (SELECT period_id FROM payroll_periods WHERE period_code = '2026-08');

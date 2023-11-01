--
-- File generated with SQLiteStudio v3.4.4 on Sun Sep 24 20:01:49 2023
--
-- Text encoding used: UTF-8
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: Programs
CREATE TABLE IF NOT EXISTS Programs (progid INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, category TEXT, description TEXT, subjects TEXT, careers TEXT);
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (1, 'Bachelor of Science in Architecture (BS-ARCH)', 'Architecture and Design', 'A 5-year course that is concerned with the design and planning of architectural 
structures such as residential and commercial buildings, roads, dams, tunnels, 
bridges, and the like.', 'Architectural Design
Architectural History and Theory
Building Construction and Materials
Architectural Technology
Structural Design and Analysis
Environmental Systems
Architectural Graphics and Visualization
Urban Planning and Design
Site Planning and Development
Building Codes and Regulations
Landscape Architecture
Interior Design
Architectural Professional Practice
Building Information Modeling (BIM)
Architectural Studio
Sustainable Architecture
Digital Design and Modeling
Architectural Acoustics
Construction Management
Architectural Preservation and Restoration
Architectural Thesis Project', 'Architectural Design
Urban Design
Community Architecture
Facility Planning
Construction Technology
Construction Management
Building Administration and Maintenance
Real Estate Development
Restoration/Conservation');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (2, 'Bachelor of Science in Interior Design (BSID)', 'Architecture and Design', 'A 4-year course that will teach students about the principles of interior 
design, space planning, application of colors, basic furniture production, and 
budget management among other things.', 'Design Fundamentals
History of Interior Design
Architectural Drafting and Drawing
Interior Design Studio
Color Theory and Application
Furniture Design
Textiles and Materials for Interiors
Lighting Design
Building Systems and Services
Computer-Aided Design (CAD)
Sustainable Design
Interior Design History and Theory
Space Planning
Kitchen and Bath Design
Professional Practice and Ethics
Interior Design Presentation
Interior Design Internship
Retail and Commercial Design
Residential Design
Hospitality and Restaurant Design
Exhibition and Event Design
Portfolio Development', 'Visual Design Manager
Auto-CAD Draftsman
Interior Designer
Interior Lighting Designer');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (3, 'Bachelor of Science in Accountancy (BSA)', 'Business', 'A 4-year course that is primarily concerned with the effective management 
of a person’s, group’s, or company’s financial resources including 
the proper ways of monitoring and documenting the flow of money or goods 
within the system and the applicable laws related to it.', 'Accounting (financial, public, managerial)
Audit
Administration
Business Laws
Taxation', 'Financial Accounting and Reporting Staff
Management Accounting Staff
Tax Accounting Staff
Internal Audit Staff
Financial Analyst
Budget Analyst
Credit Analyst
Revenue Officer
Audit Examiner
Financial Services Specialist
Accounting Instructor
Comptroller
Senior Information Systems Auditor
Senior Fraud Examiner');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (4, 'Bachelor of Science in Business Administration (BSBA)', 'Business', 'A 4-year course that will help students learn the ins and outs of running a 
business as well as the necessary traits and skills that you need to have and 
develop to become a successful business leader.', 'Financial Accounting
Managerial Accounting
Business Ethics
Business Law
Principles of Marketing
Business Finance
Microeconomics
Macroeconomics
Organizational Behavior
Human Resource Management
Operations Management
Business Statistics
Business Communication
Entrepreneurship
Strategic Management
International Business
Supply Chain Management
Business Research Methods
Information Technology for Business
Leadership and Management
Business Ethics and Social Responsibility', 'Sales Manager
Business Consultant
Financial Analyst
Market research Analyst
Human Resources Specialist
Loan Officer
Meeting, Convention, and Event Planner
Logistician
Real Estate Appraiser
Buyer or Purchasing Agent
Compensation and Benefits Analyst');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (5, 'Bachelor of Science in Entrepreneurship (BSEntrep)', 'Business', 'A four-year course offered in the College of Business, designed to provide 
undergraduate students an in-depth understanding and appreciation of new venture 
operations in small business enterprises. It aims to equip young students with 
entrepreneurial spirit to realize a vibrant and developing economy in the hands 
of the Filipinos and develop desirable qualities of leadership, social concern 
and moral values among them.', 'Entrepreneurial Management
Small Business Management
Business Planning and Feasibility Study
Marketing Management for Entrepreneurs
Financial Management for Entrepreneurs
Human Resource Management for Entrepreneurs
Business Ethics and Social Responsibility
Business Law and Regulations
Innovation and Technology Management
Product Development and Innovation
Entrepreneurial Finance and Funding
Entrepreneurship and New Venture Creation
E-Commerce and Digital Marketing
International Entrepreneurship
Family Business Management
Social Entrepreneurship
Business Research and Analysis
Entrepreneurship Capstone Project
Leadership and Negotiation Skills', 'Entrepreneur/Small Business Owner
Business Development Manager
Franchise Consultant
Sales and Marketing Manager
Business Consultant
Financial Analyst
E-commerce Entrepreneur
Venture Capitalist or Angel Investor
Market Research Analyst
Product Manager
Business Incubator Manager
Corporate Entrepreneurship Specialist
Social Entrepreneur
Retail Buyer
Online Marketing Specialist
Event Planner/Event Management Entrepreneur
Real Estate Entrepreneur
Government Entrepreneurship Development Officer');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (6, 'Bachelor of Science in Office Administration (BSOA)', 'Business', 'A four-year degree program using ladderized curriculum.  Its three areas of 
specialization are:  Corporate Transcription, Legal Transcription, and Medical 
Transcription from which students choose on his/her start on the third year 
level.  The program is designed to equip its graduates the professional skills 
and competencies needed to meet the needs of the workplaces, whether in general 
business, legal, or the medical offices.', 'Office Management
Administrative Procedures
Business Communication
Records Management
Office Technology and Systems
Office Ethics and Professionalism
Information Management
Human Resource Management
Financial Management for Offices
Office Productivity Tools
Office Planning and Layout
Customer Service Management
Business Law and Ethics
Business Writing', 'Office Administrator
Administrative Assistant
Executive Secretary
Office Manager
Records Manager
Human Resources Coordinator
Customer Service Supervisor
Event Coordinator
Data Entry Specialist
Office Systems Analyst
Business Process Analyst
Office Technology Trainer
Office Planner and Space Designer
Document Controller
Procurement Officer
Executive Assistant
Front Desk Officer
Receptionist
Office Clerk
Office Services Manager');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (7, 'Bachelor in Physical Education (BPE)', 'Education', 'A 4-year course that will train you in developing and maintaining the optimal 
physical fitness and functionality individuals. It has two majors: School 
Physical Education which is a teacher education training program and Sports and 
Wellness Management which is a program that caters to the needs of the corporate 
industry.', 'Kinesiology and Biomechanics
Exercise Physiology
Sports Psychology
Health and Wellness Education
Nutrition and Dietetics
Teaching Methods in Physical Education
Sports Coaching and Officiating
Adapted Physical Education (for students with disabilities)
Fitness Assessment and Program Design
Sports Management and Administration
Dance and Rhythmic Activities
Team Sports (e.g., basketball, volleyball)
Individual Sports (e.g., swimming, track and field)
Practicum in Physical Education', 'P.E. Teacher
Sports Coach
P.E. Department Head
Coordinator in Physical Education and Sports Programs
Fitness and Wellness Supervisor
Gym Manager
Recreation Director
Wellness Trainer
Events / Tournaments Coordinator
Sports Tourism Officer
Sports and Wellness Facilities Manager');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (8, 'Bachelor in Secondary Education (BSED)', 'Education', 'A 4-year degree program focused on preparing students to become licensed 
secondary school teachers.', 'Educational Psychology
Curriculum Development
Classroom Management
Assessment and Evaluation
Teaching Methods and Strategies
Philippine Education System and Laws
Special Education (inclusive education)
Subject-Specific Courses (e.g., Mathematics Education, English Education, Science Education, Social Studies Education)
Educational Technology and Multimedia
Practicum and Teaching Internship', 'Secondary School Teacher
Subject Area Coordinator
Private Tutor
Education Consultant
Textbook Writer or Curriculum Developer');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (9, 'Bachelor in Elementary Education (BEED)', 'Education', 'A 4-year course  focused on preparing students to become licensed 
elementary school teachers and will train you in teaching grade school students.', 'Child and Adolescent Psychology
Educational Psychology
Educational Technology
Childhood Education
Filipino Language and Literature
English Language and Literature
Reading and Language Arts
Elementary Mathematics
Elementary Science
Araling Panlipunan (Social Studies)
MAPEH (Music, Arts, Physical Education, and Health)
Teaching Internship', 'Elementary School Teacher
Special Education Teacher
Reading Specialist
Mathematics Specialist
English Language Teacher
Science Teacher
Social Studies Teacher
Subject Coordinator
Teacher Trainer
School Administrator
Guidance Counselor
Education Consultant
Private Tutor
Education Researcher
Curriculum Developer
Textbook Writer
Parent Educator
Education Blogger or Writer');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (10, 'Bachelor of Science in Civil Engineering (BSCE)', 'Engineering', 'A 5-year course that is concerned with the use of scientific and mathematical 
principles in the construction of buildings and infrastructures such as roads, 
bridges, tunnels, dams, airports, and the like.', 'Surveying
Building Design
Advanced Engineering Mathematics for Civil Engineering
Structural Theory
Structural Design
Hydraulics
Hydrology
Water Resources Engineering
Highway Engineering
Computer Aided Drafting', 'City Planner
Structural Engineer
Traffic Engineer
Water Resources Engineer
Contractor');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (11, 'Bachelor of Science in Computer Engineering (BSCpE)', 'Engineering', 'A 5-year course that will train you in the design, development, and maintenance 
of computer systems including both hardware and software.', 'Computer Engineering Drafting and Design
Control Systems
Computer Systems and Architecture
Computer Sytems and Oragnization with Assembly Language
Computer Networks
Data Structure and Algorithm Analysis
Operating Systems
Engineering Ethics and Computer Laws
Computer Hardware Fundamentals
Advanced Logic Circuits
Digital Signal Processing
Electronic Circuits Analysis and Design', 'Network systems administrator/manager
Computer Systems Manager
Quality Assurance Manager
Senior Communications Engineer
Systems Analyst
Network Architects
Systems Engineer
Computer Consultant
Support Specialist
Instrumentation Technician
Applications Analyst
Printed Circuit designer
Electrical Designer
Robotics Control Systems Engineer');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (12, 'Bachelor of Science in Electrical Engineering (BSEE)', 'Engineering', 'A 5-year course that is concerned with electricity, its production, 
transmission, distribution, and usage. Its curriculum covers everything from the 
design of electrical systems up to their operation and maintenance as well as 
cheaper and safer ways of completing engineering projects.', 'Basic Thermodynamics
Electronics Circuits and Devices
Electronic Circuits Analysis and Design
Industrial Electronics
Electromagenetics
Logic Circuits and Switching Theory
Microprocessor Systems
Control Systems Analysis
Electrical Transmission and Distribution System
Illumination Engineering Design
Electrical Equipment: Operation and Maintenance
Power System Analysis and Design', 'Project Engineer
Network Systems Administrator
Data Communications Engineer
Systems Engineer
Systems Developer
Systems Analyst
Systems Designer
Technical Support Engineer
Quality Assurance Engineer
College Instructor
Researcher');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (13, 'Bachelor of Science in Electronics Engineering (BSECE)', 'Engineering', 'A four-year undergraduate degree program that provides 
students with a strong foundation in electronics and electrical engineering 
principles. It encompasses both theoretical knowledge and practical skills 
necessary for designing, analyzing, and troubleshooting electronic systems and 
devices.', 'Circuit Analysis
Digital Electronics
Analog Electronics
Electromagnetic Theory
Control Systems
Microelectronics
Semiconductor Physics
Signal Processing
Communication Systems
Power Electronics
Embedded Systems
Computer Programming (C/C++)
Electrical Machines and Drives', 'Electronics Engineer
Hardware Engineer
Control Systems Engineer
Telecommunications Engineer
Embedded Systems Engineer
Power Electronics Engineer');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (14, 'Bachelor of Science in Industrial Engineering (BSIE)', 'Engineering', 'A five-year baccalaureate degree program offered in the College of Engineering 
(CE). Industrial Engineering as a name is used in industry, commerce, and 
government maybe the broadcast of all the modern management functions that if 
consists of all engineering and management control activities that cannot be 
clearly designated as a part of other engineering or accounting functions. It is 
a large umbrella that includes a wide variety of task established for the 
purpose of designing, implementing and maintaining management system for 
effective operations.', 'Production and Operations Management
Quality Management
Human Factors Engineering (Ergonomics)
Supply Chain Management
Project Management
Environmental and Sustainability Engineering
Lean Manufacturing
Occupational Safety and Health Management
Logistics and Transportation
Data Analytics and Industrial Engineering', 'Project Manager
Strategic Planner
Process Developer');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (15, 'Bachelor of Science in Mechanical Engineering (BSME)', 'Engineering', 'A 4-year course that focuses on the fundamental knowledge and skills of 
mechanical engineering. This revolves around the design, production, and 
maintenance of machines from simple home appliances, gadgets, and automobiles, 
to more complicated industrial equipment, robots, and jet engines.', 'Thermodynamics
Fluid Mechanics
Heat Transfer
Solid Mechanics
Machine Design
Materials Science
Engineering Mathematics
Engineering Drawing and CAD
Manufacturing Processes
Dynamics and Control Systems
Mechanical Vibrations
Engineering Economics and Management
Engineering Ethics', 'Power and Energy Engineering
Automotive Engineering
Manufacturing Engineering
Mechatronics and Robotics
Heating, Ventilating, Air-conditioning and Refrigeration
Biomedical Engineering
Instrumentation and Controls');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (16, 'Bachelor of Science in Information Technology (BSIT)', 'Formal Sciences', 'A four-year degree program which focuses  on the study of computer utilization
and computer software to plan, install, customize, operate, manage, administer
and maintain information technology infrastructure. It likewise deals with the design and development of computer-based information systems for real-world 
business solutions.', 'IT Fundamentals
Programming
Discrete Structure
Computer Organization
Operating Systems Application
Network Management
System Analysis and Design
Software Engineering
Technopreneurship
Onject Oriented Programming
Database Management System', 'Researcher
IT Project Manager
Information Technology
Instructor
Entrepreneur in IT Industry
Information Security Administrator
Junior Software Engineer
Junior Systems Analyst
Systems Developer
Applications Developer
Web Developer
Database Administrator');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (17, 'Bachelor of Science in Computer Science (BSCS)', 'Formal Sciences', 'A four-year degree course, which focuses on the study of concepts and theories, 
algorithmic foundations, implementation and application of information and 
computing solutions. The program prepares students to be IT professionals and 
Researchers, and to be proficient in designing and developing computing 
solutions.', 'Computer Organization And Assembly Languages
Data Structure and Algorithms
Design and Analysis Algorithms
Programming Languages
Automata and Language Theory
Modelling and Simulation
Digital Design
Operating Systems
Network
Objecct Oriented Programminng
Database Systems
Web Programming', 'Junior Software Engineer
Junior Systems Analyst
Systems Developer
Applications Developer
Quality Assurance Engineer
Information Security Engineer
Researcher
Network Specialist
Computer Science Instructor
Systems Administrator
Systems Analyst');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (18, 'Bachelor of Arts in Philosophy (ABPHILO)', 'Humanities', 'A 4-year course that teaches you the underlying principles of reality,
knowledge, morality, and existence and how it applies to other fields.', 'Introduction to Philosophy
Oriental Philosophy
History of Western Philosophy
Philosophy of Religion
Philosophy of Human Person
Philosophy of Science & Technology
Epistemology
Philosophy and History of Ecology
Filipino Philosophy
Metaphysics
Philosophy of Law
Environmental Philosophy
Seminar on Environmental Ethics, and other Issues
Philosophy of Education', 'Critique
Political Analyst/Adviser
Teacher
Philosophical Counselor
Debater
Philosophy teacher
Researcher');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (19, 'Bachelor of Arts in Journalism (ABJ)', 'Media and Communication', 'A 4-year course designed to prepare students for careers as media practitioners.  
Specifically, it intends to provide training in the field of journalism with the 
end in view of inculcating the desired values of the profession.', 'Journalism Ethics and Standards
Media Law and Ethics
News Writing and Reporting
Feature Writing
Photojournalism
Broadcast Journalism
Digital Journalism
Investigative Journalism
Media and Society
Media Production
Media Management and Entrepreneurship
International Journalism
Editorial and Opinion Writing
Media Research and Analysis
Media and Communication Theories', 'News Editor
Media Researcher
News Analyst
Writers
Broadcast Journalist
Photojournalist
Program Director
Public Relations Officer
Proofreader
Reporter
Web Content Writer');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (20, 'Bachelor of Arts on Broadcasting (BA Broadcasting)', 'Media and Communication', 'A 4-year course that will prepare students for a career in the media industry, 
particularly in television and radio networks. It is a field of study that 
traditionally covers the transmission of messages to the public through the 
media of radio and television. This field has expanded and include non-
traditional internet-based multi-platform media.', 'Broadcast Journalism
Radio and TV Production
Broadcasting Ethics and Regulations
Media Writing
Media Performance and Presentation
Radio and TV News Production
Broadcast Management
Media Research and Analysis
Broadcasting Technology
Media and Communication Theories
Digital Broadcasting
Advertising and Public Relations
Media Production Management
Media Internship
Media Criticism and Analysis', 'Radio or Television Writer
Radio or Television Director
Radio or Television Producer
News Reporter/News Anchor
Media Manager/Media Consultant
College Professor');
INSERT INTO Programs (progid, title, category, description, subjects, careers) VALUES (21, 'Bachelor of Science in Economics (BS Economics)', 'Social Sciences', 'A 4-year course that will help you gain a better understanding of economic
systems and their structure. This includes the entities that compose them, 
their relationship with one another, and how the introduction of external
factors can affect not only them but the entire system as well.', 'Basic Macroecomics
Basic Microecomics
Itermediate Microeconomics
Econometrics
International Economics
Development Economics
Philippine Economic History
Urban Economics
Regional Economics
Money and Banking
Industrial Organization', 'Analyst (Economic, Market, Research)
 Bank Analyst
 Economics Consultant
 Economics Journalist
 Economist
 Corporate Planner
 Researcher
 University Instructor');

COMMIT TRANSACTION;
PRAGMA foreign_keys = on;

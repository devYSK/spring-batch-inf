# spring-batch-inf

인프런 스프링 배치 강좌 학습 및 정리 - 

1. 스프링 배치 소개
2. 스프링 배치 시작
3. 스프링 배치 도메인 이해
4. 스프링 배치 실행
5. 스프링 배치 청크 프로세스 (1)
6. 스프링 배치 청크 프로세스 (2)
7. 스프링 배치 반복 및 오류 제어
8. 스프링 배치 멀티 스레드 프로세싱  
9. 스프링 배치 이벤트 리스너
10. 스프링 배치 테스트 및 운영   
11. 실전 ! 스프링 배치


# 1. 스프링 배치 소개
1. 스프링 배치 탄생 배경
   * 자바 기반 표준 배치 기술 부재
   * 배치 처리에서 요구하는 재사용 가능한 자바 기반 배치 아키텍처 표준의 필요성이 대두
   * 스프링 배치는 SpringSource(현재는 Pivotal)와 Accenture(경영 컨설팅 기업) 의 합작품
   * Accenture - 배치 아키텍처를 구현하면서 쌓은 기술적인 경험과 노하우
   * SpringSource - 깊이 있는 기술적 기반과 스프링의 프로그래밍 모델
   * Accenture는 이전에 소유했던 배치 처리 아키텍처 프레임워크를 Spring Batch 프로젝트에 기증함
   * https://docs.spring.io/spring-batch/docs/4.3.x/reference/html/spring-batch-intro.html#spring-batch-intro

2. ### ` 배치 핵심 패턴`
   * `Read - 데이터베이스, 파일, 큐에서 다량의 데이터 조회한다.`
   * `Process - 특정 방법으로 데이터를 가공한다.`
   * `Write - 데이터를 수정된 양식으로 다시 저장한다.`


3. 배치 시나리오
   * 배치 프로세스를 주기적으로 커밋
   * 동시 다발적인 Job 의 배치 처리, 대용량 병렬 처리
   * 실패 후 수동 또는 스케줄링에 의한 재시작
   * 의존관계가 있는 step 여러 개를 순차적으로 처리
   * 조건적 Flow 구성을 통한 체계적이고 유연한 배치 모델 구성
   * 반복, 재시도, Skip 처리

* ![](img/96a04155.png)

# 2. 스프링 배치 시작

## 스프링 배치 활성화

* @EnableBatchProcessing
  * 스프링 배치가 작동하기 위해 선언해야 하는 어노테이션
* ![](img/195e5c2a.png)
* 총 4개의 설정 클래스를 실행시키며 스프링 배치의 모든 초기화 및 실행 구성이 이루어진다

* 스프링 부트 배치의 자동 설정 클래스가 실행됨으로 빈으로 등록된 모든 Job 을 검색해서 초기화와 동시에 Job 을 수행하도록 구성됨

## `스프링 배치 초기화 설정 클래스!`
![](img/f884d6f3.png)

1. BatchAutoConfiguration
   * 스프링 배치가 초기화 될 때 자동으로 실행되는 설정 클래스
   * Job 을 수행하는 `JobLauncherApplicationRunner 빈을 생성`

2. SimpleBatchConfiguration
   * `JobBuilderFactory 와 StepBuilderFactory 생성`
   * 스프링 배치의 주요 구성 요소 생성 - 프록시 객체로 생성됨

3. BatchConfigurerConfiguration
   * BasicBatchConfigurer
   * SimpleBatchConfiguration 에서 생성한 프록시 객체의 실제 대상 객체를 생성하는 설정 클래스
   * 빈으로 의존성 주입 받아서 주요 객체들을 참조해서 사용할 수 있다
   * JpaBatchConfigurer
   * JPA 관련 객체를 생성하는 설정 클래스
   * 사용자 정의 BatchConfigurer 인터페이스를 구현하여 사용할 수 있음

## BatchConfiguration 
1. @Configuration 선언
   * 하나의 배치 Job 을 정의하고 빈 설정
2. JobBuilderFactory
   * Job 을 생성하는 빌더 팩토리
3. StepBuilderFactory
   * Step 을 생성하는 빌더 팩토리
4. Job
   * helloJob 이름으로 Job 생성
5. Step
   * helloStep 이름으로 Step 생성
6. tasklet
   * Step 안에서 단일 태스크로 수행되는 로직 구현
7. Job 구동 -> Step 을 실행 -> Tasklet 을 실행

### Configuration 프로세스

* ![](img/b4385822.png)
* ![](img/94b82676.png)

## 스프링 배치 DB스키마

1. 스프링 배치 메타 데이터
   * 스프링 배치의 실행 및 관리를 위한 목적으로 여러 도메인들(Job, Step, JobParameters..) 의 정보들을 저장, 업데이트, 조회할 수 있는 스키마 제공
   * 과거, 현재의 실행에 대한 세세한 정보, 실행에 대한 성공과 실패 여부 등을 일목요연하게 관리함으로서 배치운용에 있어 리스크 발생시 빠른 대처 가능
   * DB 와 연동할 경우 필수적으로 메타 테이블이 생성 되어야 함
2. DB 스키마 제공
   * 파일 위치 : /org/springframework/batch/core/schema-*.sql
   * DB 유형별로 제공
3. 스키마 생성 설정
   * 수동 생성 – 쿼리 복사 후 직접 실행
   * 자동 생성 - spring.batch.jdbc.initialize-schema 설정
   * ALWAYS
   * 스크립트 항상 실행
   * RDBMS 설정이 되어 있을 경우 내장 DB 보다 우선적으로 실행
   * EMBEDDED : 내장 DB일 때만 실행되며 스키마가 자동 생성됨, 기본값
   * NEVER
   * 스크립트 항상 실행 안함
   * 내장 DB 일경우 스크립트가 생성이 안되기 때문에 오류 발생
     * 이경우 우리가 만들어 줘야 한다. 
   * 운영에서 수동으로 스크립트 생성 후 설정하는 것을 권장
      * 파일 위치 : /org/springframework/batch/core/schema-*.sql 에서 복사 가능! 

* ## Job 관련 테이블
* BATCH_JOB_INSTANCE
  * Job 이 실행될 때 JobInstance 정보가 저장되며 job_name과 job_key를 키로 하여 하나의 데이터가 저장
    * 동일한 job_name 과 job_key 로 중복 저장될 수 없다

  * ```sql
    CREATE TABLE BATCH_JOB_INSTANCE (
     JOB_INSTANCE_ID BIGINT PRIMARY KEY ,   # 고유하게 식별할 수 있는 기본 키
     VERSION BIGINT,                        # 업데이트 될 때 마다 1씩 증가
     JOB_NAME VARCHAR(100) NOT NULL ,       # Job 을 구성할 때 부여하는 Job 의 이름 
     JOB_KEY VARCHAR(2500)                  # job_name과 jobParameter 를 합쳐 해싱한 값을 저장
    );
    ```
---
* BATCH_JOB_EXECUTION
  * job 의 실행정보가 저장되며 Job 생성, 시작, 종료 시간, 실행상태, 메시지 등을 관리
  * ```sql
    CREATE TABLE BATCH_JOB_EXECUTION (
     JOB_EXECUTION_ID BIGINT PRIMARY KEY ,          # JobExecution 을 고유하게 식별할 수 있는 기본 키, JOB_INSTANCE 와 일대 다 관계
     VERSION BIGINT,                                # 업데이트 될 때마다 1씩 증가
     JOB_INSTANCE_ID BIGINT NOT NULL,               # JOB_INSTANCE 의 키 저장
     CREATE_TIME TIMESTAMP NOT NULL,                # 실행(Execution)이 생성된 시점을 TimeStamp 형식으로 기록 
     START_TIME TIMESTAMP DEFAULT NULL,             # 실행(Execution)이 시작된 시점을 TimeStamp 형식으로 기록
     END_TIME TIMESTAMP DEFAULT NULL,               # 실행이 종료된 시점을 TimeStamp으로 기록 Job 실행 도중 오류가 발생해서 Job 이 중단된 경우 값이 저장되지 않을 수 있음
     STATUS VARCHAR(10),                            # 실행 상태 (BatchStatus)를 저장 (COMPLETED, FAILED, STOPPED…)
     EXIT_CODE VARCHAR(20),                         # 실행 종료코드(ExitStatus) 를 저장 (COMPLETED, FAILED…)
     EXIT_MESSAGE VARCHAR(2500),                    # Status가 실패일 경우 실패 원인 등의 내용을 저장
     LAST_UPDATED TIMESTAMP,                        # 마지막 실행(Execution) 시점을 TimeStamp 형식으로 기록
     JOB_CONFIGURATION_LOCATION VARCHAR(2500) NULL
    );
    ```

---

* BATCH_JOB_EXECUTION_PARAMS
  * Job과 함께 실행되는 JobParameter 정보를 저장
  * ```sql
    CREATE TABLE BATCH_JOB_EXECUTION_PARAMS (
     JOB_EXECUTION_ID BIGINT NOT NULL ,     # JobExecution 식별 키, JOB_EXECUTION 과는 일대다 관계
     TYPE_CD VARCHAR(6) NOT NULL ,          # STRING, LONG, DATE, DUBLE 타입정보
     KEY_NAME VARCHAR(100) NOT NULL ,       # 파라미터 키 값
     STRING_VAL VARCHAR(250) ,              # 파라미터 문자 값
     DATE_VAL DATETIME DEFAULT NULL ,       # 파라미터 날짜 값
     LONG_VAL BIGINT ,                      # 파라미터 LONG 값
     DOUBLE_VAL DOUBLE PRECISION ,          # 파라미터 DOUBLE 값
     IDENTIFYING CHAR(1) NOT NULL           # 식별 여부(TRUE, FALSE)
    );
    ``` 
---

* BATCH_JOB_EXECUTION_CONTEXT
  * Job 의 실행동안 여러가지 상태정보, 공유 데이터를 직렬화 (Json 형식) 해서 저장
  * Step 간 서로 공유 가능함
  * ```sql
    CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT (
     JOB_EXECUTION_ID BIGINT PRIMARY KEY,   # JobExecution 식별 키, JOB_EXECUTION 마다 각 생성
     SHORT_CONTEXT VARCHAR(2500) NOT NULL,  # JOB 의 실행 상태정보, 공유데이터 등의 정보를 문자열로 저장 
     SERIALIZED_CONTEXT CLOB                # 직렬화(serialized)된 전체 컨텍스트 
    );
    ```
  
---
* ## Step 관련 테이블
  * BATCH_STEP_EXECUTION
    * Step 의 실행정보가 저장되며 생성, 시작, 종료 시간, 실행상태, 메시지 등을 관리
  * ```sql
    CREATE TABLE BATCH_STEP_EXECUTION (
     STEP_EXECUTION_ID BIGINT PRIMARY KEY , # Step 의 실행정보를 고유하게 식별할 수 있는 기본 키
     VERSION BIGINT NOT NULL,               # 업데이트 될 때마다 1씩 증가
     STEP_NAME VARCHAR(100) NOT NULL,       # Step 을 구성할 때 부여하는 Step 이름
     JOB_EXECUTION_ID BIGINT NOT NULL,      #  JobExecution 기본키, JobExecution 과는 일대 다 관계
     START_TIME TIMESTAMP NOT NULL ,        # 실행(Execution)이 시작된 시점을 TimeStamp 형식으로 기록
     END_TIME TIMESTAMP DEFAULT NULL,       # 실행이 종료된 시점을 TimeStamp 으로 기록하며 Job 실행 도중 오류가 발생해서 Job 이 중단된 경우 값이 저장되지 않을 수 있음
     STATUS VARCHAR(10),                    # 실행 상태 (BatchStatus)를 저장 (COMPLETED, FAILED, STOPPED…)
     COMMIT_COUNT BIGINT ,                  # 트랜잭션 당 커밋되는 수를 기록
     READ_COUNT BIGINT ,                    # 실행시점에 Read한 Item 수를 기록
     FILTER_COUNT BIGINT ,                  # 실행도중 필터링된 Item 수를 기록
     WRITE_COUNT BIGINT ,                   # 실행도중 저장되고 커밋된 Item 수를 기록
     READ_SKIP_COUNT BIGINT ,               # 실행도중 Read가 Skip 된 Item 수를 기록
     WRITE_SKIP_COUNT BIGINT ,              # 실행도중 write가 Skip된 Item 수를 기록
     PROCESS_SKIP_COUNT BIGINT ,            # 실행도중 Process가 Skip 된 Item 수를 기록
     ROLLBACK_COUNT BIGINT ,                # 실행도중 rollback이 일어난 수를 기록
     EXIT_CODE VARCHAR(20) ,                # 실행 종료코드(ExitStatus) 를 저장 (COMPLETED, FAILED…)
     EXIT_MESSAGE VARCHAR(2500) ,           # Status가 실패일 경우 실패 원인 등의 내용을 저장
     LAST_UPDATED TIMESTAMP                 # 마지막 실행(Execution) 시점을 TimeStamp 형식으로 기록
    );
    ```
---
  * BATCH_STEP_EXECUTION_CONTEXT
    * Step 의 실행동안 여러가지 상태정보, 공유 데이터를 직렬화 (Json 형식) 해서 저장
    * Step 별로 저장되며 Step 간 서로 공유할 수 없음
    * ```sql
      CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT (
       STEP_EXECUTION_ID BIGINT PRIMARY KEY,    # StepExecution 식별 키, STEP_EXECUTION 마다 각 생성
       SHORT_CONTEXT VARCHAR(2500) NOT NULL,    # STEP 의 실행 상태정보, 공유데이터 등의 정보를 문자열로 저장 
       SERIALIZED_CONTEXT CLOB                  # 직렬화(serialized)된 전체 컨텍스트
      );
      ```
---

# 3. 스프링 배치 도메인 이해

## Job 

1. 기본 개념
   * 배치 계층 구조에서 가장 상위에 있는 개념으로서 하나의 배치작업 자체를 의미함
     * “API 서버의 접속 로그 데이터를 통계 서버로 옮기는 배치“ 인 Job 자체를 의미한다.
   * Job Configuration 을 통해 생성되는 객체 단위로서 배치작업을 어떻게 구성하고 실행할 것인지 전체적으로 설정하고 명세해 놓은 객체
   * 배치 Job 을 구성하기 위한 최상위 인터페이스이며 스프링 배치가 기본 구현체를 제공한다
   * 여러 Step 을 포함하고 있는 컨테이너로서 반드시 한개 이상의 Step으로 구성해야 함

2. 기본 구현체
   * SimpleJob
     * 순차적으로 Step 을 실행시키는 Job
     * 모든 Job에서 유용하게 사용할 수 있는 표준 기능을 갖고 있음
   
   * FlowJob
     * 특정한 조건과 흐름에 따라 Step 을 구성하여 실행시키는 Job
     * Flow 객체를 실행시켜서 작업을 진행함


* ![](img/b3ed6f4e.png)
* JobLauncher가 Job을 실행시킨다. 


## JobInstance
1. 기본 개념
   * Job 이 실행될 때 생성되는 Job 의 논리적 실행 단위 객체로서 고유하게 식별 가능한 작업 실행을 나타냄
   * Job 의 설정과 구성은 동일하지만 Job 이 실행되는 시점에 처리하는 내용은 다르기 때문에 Job 의 실행을 구분해야 함
     * 예를 들어 하루에 한 번 씩 배치 Job이 실행된다면 매일 실행되는 각각의 Job 을 JobInstance 로 표현합니다.
   * JobInstance 생성 및 실행
     * 처음 시작하는 Job + JobParameter 일 경우 새로운 JobInstance 생성
     * 이전과 동일한 Job + JobParameter 으로 실행 할 경우 이미 존재하는 JobInstance 리턴 -> 재사
       * 내부적으로 JobName + jobKey (jobParametes 의 해시값) 를 가지고 JobInstance 객체를 얻음
   * Job 과는 1:M 관계


2. BATCH_JOB_INSTANCE 테이블과 매핑
   * JOB_NAME (Job) 과 JOB_KEY (JobParameter 해시값) 가 동일한 데이터는 중복해서 저장할 수 없음

* JobInstance 흐름도
* ![](img/f3e15172.png)
* ![](img/a0b38896.png)


## JobParameters

1. 기본 개념
   * Job을 실행할 때 함께 포함되어 사용되는 파라미터를 가진 도메인 객체
   * 하나의 Job에 존재할 수 있는 여러개의 JobInstance를 구분하기 위한 용도
   * JobParameters와 JobInstance는 1:1 관계

2. 생성 및 바인딩
   * 어플리케이션 실행 시 주입
     * Java -jar LogBatch.jar requestDate=20210101
   * 코드로 생성
     * JobParameterBuilder, DefaultJobParametersConverter
   * SpEL 이용
     * @Value(“#{jobParameter[requestDate]}”), @JobScope, @StepScope 선언 필수

3. BATCH_JOB_EXECUTION_PARAM 테이블과 매핑
   • JOB_EXECUTION 과 1:M 의 관계

* ![](img/407e3ecb.png)

## JobExecution


1. 기본 개념
   * JobIstance 에 대한 한번의 시도를 의미하는 객체로서 Job 실행 중에 발생한 정보들을 저장하고 있는 객체
     * 시작시간, 종료시간 ,상태(시작됨,완료,실패),종료상태의 속성을 가짐
   * JobIstance 과의 관계
     * JobExecution은 'FAILED' 또는 'COMPLETED‘ 등의 Job의 실행 결과 상태를 가지고 있음
     * JobExecution 의 실행 상태 결과가 'COMPLETED’ 면 JobInstance 실행이 완료된 것으로 간주해서 재 실행이 불가함
     * JobExecution 의 실행 상태 결과가 'FAILED’ 면 JobInstance 실행이 완료되지 않은 것으로 간주해서 재실행이 가능함
       * JobParameter 가 동일한 값으로 Job 을 실행할지라도 JobInstance 를 계속 실행할 수 있음
   * JobExecution 의 실행 상태 결과가 'COMPLETED’ 될 때까지 하나의 JobInstance 내에서 여러 번의 시도가 생길 수 있음

2. BATCH_JOB_EXECUTION 테이블과 매핑
   * JobInstance 와 JobExecution 는 1:M 의 관계로서 JobInstance 에 대한 성공/실패의 내역을 가지고 있음


* ![](img/d154eea4.png)
* ![](img/2a125869.png)
* ![](img/e770e98c.png)

---

## Step

1. 기본 개념
   * Batch job을 구성하는 독립적인 하나의 단계로서 실제 배치 처리를 정의하고 컨트롤하는 데 필요한 모든 정보를 가지고 있는 도메인 객체
   * 단순한 단일 태스크 뿐 아니라 입력과 처리 그리고 출력과 관련된 복잡한 비즈니스 로직을 포함하는 모든 설정들을 담고 있다.
   * 배치작업을 어떻게 구성하고 실행할 것인지 Job 의 세부 작업을 Task 기반으로 설정하고 명세해 놓은 객체
   * 모든 Job은 하나 이상의 step으로 구성됨


2. 기본 구현체
   * TaskletStep
     * 가장 기본이 되는 클래스로서 Tasklet 타입의 구현체들을 제어한다
   * PartitionStep
     * 멀티 스레드 방식으로 Step 을 여러 개로 분리해서 실행한다
   * JobStep
     * Step 내에서 Job 을 실행하도록 한다
   * FlowStep
     * Step 내에서 Flow 를 실행하도록 한다


* Step class 구조
  * ![](img/f2d157db.png) 
  * ![](img/f0704241.png)

* ### API 설정에 따른 각 Step 생성
  * ![](img/48669e7a.png)

## StepExecution

1. 기본 개념
   * Step 에 대한 한번의 시도를 의미하는 객체로서 Step 실행 중에 발생한 정보들을 저장하고 있는 객체
     * 시작시간, 종료시간 ,상태(시작됨,완료,실패), commit count, rollback count 등의 속성을 가짐
   * Step 이 매번 시도될 때마다 생성되며 각 Step 별로 생성된다
   * `Job 이 재시작 하더라도 이미 성공적으로 완료된 Step 은 재 실행되지 않고 실패한 Step 만 실행된다`
   * `이전 단계 Step이 실패해서 현재 Step을 실행하지 않았다면 StepExecution을 생성하지 않는다. Step이 실제로 시작됐을 때만 StepExecution을 생성한다`
   * JobExecution 과의 관계
     * Step의 StepExecution 이 모두 정상적으로 완료 되어야 JobExecution이 정상적으로 완료된다.
     * Step의 StepExecution 중 하나라도 실패하면 JobExecution 은 실패한다
2. BATCH_STEP_EXECUTION 테이블과 매핑
   * JobExecution 와 StepExecution 는 1:M 의 관계
   * 하나의 Job 에 여러 개의 Step 으로 구성했을 경우 각 StepExecution 은 하나의 JobExecution 을 부모로 가진다


* ![](img/93c15f85.png)

* StepExecution class
  * ![](img/f9d60b69.png) 

## StepContribution

1. 기본 개념
   * 청크 프로세스의 변경 사항을 버퍼링 한 후 StepExecution 상태를 업데이트하는 도메인 객체
   * 청크 커밋 직전에 StepExecution 의 apply 메서드를 호출하여 상태를 업데이트 함
   * ExitStatus 의 기본 종료코드 외 사용자 정의 종료코드를 생성해서 적용 할 수 있음

2. 구조
   * ![](img/d1388956.png)
   
* StepContribution 흐름
  *  ![](img/1ee0f972.png)

## ExecutionContext

1. 기본 개념
   * 프레임워크에서 유지 및 관리하는 키/값으로 된 컬렉션으로 StepExecution 또는 JobExecution 객체의 상태(state)를 저장하는 공유 객체
   * DB 에 직렬화 한 값으로 저장됨 - { “key” : “value”}
   * 공유 범위
     * Step 범위 – 각 Step 의 StepExecution 에 저장되며 Step 간 서로 공유 안됨
     * Job 범위 – 각 Job의 JobExecution 에 저장되며 Job 간 서로 공유 안되며 해당 Job의 Step 간 서로 공유됨
   * Job 재 시작시 이미 처리한 Row 데이터는 건너뛰고 이후로 수행하도록 할 때 상태 정보를 활용한다

2. 구조

* ![](img/6b05106c.png)

* ![](img/dcc480ae.png)

* ![](img/0766df42.png)


## JobRepository

1. 기본 개념
   * 배치 작업 중의 정보를 저장하는 저장소 역할
   * Job이 언제 수행되었고, 언제 끝났으며, 몇 번이 실행되었고 실행에 대한 결과 등의 배치 작업의 수행과 관련된 모든 meta data 를 저장함
     * JobLauncher, Job, Step 구현체 내부에서 CRUD 기능을 처리함
   
   * ![](img/c328e403.png)

* JobRepository class
  * ![](img/66a593cc.png) 

* JobRepository 설정
  * @EnableBatchProcessing 어노테이션만 선언하면 JobRepository 가 자동으로 빈으로 생성됨
  * BatchConfigurer 인터페이스를 구현하거나 BasicBatchConfigurer 를 상속해서 JobRepository 설정을 커스터마이징 할 수 있다
    * JDBC 방식으로 설정- JobRepositoryFactoryBean
      * 내부적으로 AOP 기술를 통해 트랜잭션 처리를 해주고 있음
      * 트랜잭션 isolation 의 기본값은 SERIALIZEBLE 로 최고 수준, 다른 레벨(READ_COMMITED, REPEATABLE_READ)로 지정 가능
      * 메타테이블의 Table Prefix 를 변경할 수 있음, 기본 값은 “BATCH_” 임

* In Memory 방식으로 설정 – MapJobRepositoryFactoryBean
  * 성능 등의 이유로 도메인 오브젝트를 굳이 데이터베이스에 저장하고 싶지 않을 경우
  * 보통 Test 나 프로토타입의 빠른 개발이 필요할 때 사용

1. JDBC 방식
```java
@Override
protected JobRepository createJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.setIsolationLevelForCreate("ISOLATION_SERIALIZABLE"); // isolation 수준, 기본값은 “ISOLATION_SERIALIZABLE”
        factory.setTablePrefix(“SYSTEM_"); // 테이블 Prefix, 기본값은 “BATCH_”, BATCH_JOB_EXECUTION 가 SYSTEM_JOB_EXECUTION 으로 변경됨
        factory.setMaxVarCharLength(1000); // varchar 최대 길이(기본값 2500)
        return factory.getObject(); // Proxy 객체가 생성됨 (트랜잭션 Advice 적용 등을 위해 AOP 기술 적용)
}
```

2. In Memory 방식
```java
@Override
protected JobRepository createJobRepository() throws Exception {
    MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean();
    factory.setTransactionManager(transactionManager); // ResourcelessTransactionManager 사용
    return factory.getObject();
}
```

---

## JobLauncher

1. 기본 개념
   * 배치 Job 을 실행시키는 역할을 한다
   * Job과 Job Parameters를 인자로 받으며 요청된 배치 작업을 수행한 후 최종 client 에게 JobExecution을 반환함
   * 스프링 부트 배치가 구동이 되면 JobLauncher 빈이 자동 생성 된다
   * Job 실행
     * `JobLanucher.run(Job, JobParameters)`
     * 스프링 부트 배치에서는 JobLauncherApplicationRunner 가 자동적으로 JobLauncher 을 실행시킨다
     * 동기적 실행
       * taskExecutor 를 SyncTaskExecutor 로 설정할 경우 (기본값은 SyncTaskExecutor)
       * JobExecution 을 획득하고 배치 처리를 최종 완료한 이후 Client 에게 JobExecution 을 반환
       * 스케줄러에 의한 배치처리에 적합 함 – 배치처리시간이 길어도 상관없는 경우
     
     * 비 동기적 실행
       * taskExecutor 가 SimpleAsyncTaskExecutor 로 설정할 경우
       * JobExecution 을 획득한 후 Client 에게 바로 JobExecution 을 반환하고 배치처리를 완료한다
       * HTTP 요청에 의한 배치처리에 적합함 – 배치처리 시간이 길 경우 응답이 늦어지지 않도록 함

* ![](img/9d3a40b7.png)

# 4. 스프링 배치 실행

# 5. 스프링 배치 청크 프로세스 (1)

# 6. 스프링 배치 청크 프로세스 (2)

# 7. 스프링 배치 반복 및 오류 제어

# 8. 스프링 배치 멀티 스레드 프로세싱

# 9. 스프링 배치 이벤트 리스너

# 10. 스프링 배치 테스트 및 운영

# 11. 실전 ! 스프링 배치


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



# 3. 스프링 배치 도메인 이해

# 4. 스프링 배치 실행

# 5. 스프링 배치 청크 프로세스 (1)

# 6. 스프링 배치 청크 프로세스 (2)

# 7. 스프링 배치 반복 및 오류 제어

# 8. 스프링 배치 멀티 스레드 프로세싱

# 9. 스프링 배치 이벤트 리스너

# 10. 스프링 배치 테스트 및 운영

# 11. 실전 ! 스프링 배치


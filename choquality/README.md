## Application Architecture
* 마이크로 서비스 구조로 추후 변경이 쉽도로 별도의 모듈식 프로젝트 구성

1. 프로젝트 구조
````
choquality/
│
├─ BuildComponent/                          # component 모듈 위치
│  ├─ common/                               # common 모듈
│  │  └─ com.example.choquality.common
│  │     ├─ advise/                         # RestControllerAdvice 설정 
│  │     ├─ config/                         # 인증/DB/JWT/Security/Web 설정
│  │     ├─ constant/                       # 상수 정의
│  │     ├─ controller/                     # 컨트롤러 계층 (API, 요청 처리)
│  │     ├─ dto/                            # Data Transfer Object
│  │     ├─ exception/                      # 예외 처리
│  │     ├─ filter/                         # 필터 관련 처리
│  │     ├─ jpa/                            # JPA(entity,repository) 처리
│  │     ├─ jwt/                            # JWT 인터페이스 파일
│  │     ├─ mapper/                         # entity 확장 fun 처리
│  │     ├─ service/                        # common 모듈 내 Service 정의
│  │     ├─ spec/                           # 에러/성공 상태 코드와 메시지를 정의
│  │     └─ user/                           # custom 인증 유저 
│  │     
│  └─ todo/                                 # todo 모듈
│     └─ com.example.choquality.common
│        ├─ advise/                         # RestControllerAdvice 설정 
│        ├─ controller/                     # 컨트롤러 계층 (API, 요청 처리)
│        ├─ dto/                            # Data Transfer Object
│        ├─ mapper/                         # entity 확장 fun 처리
│        └─ service/                        # todo 모듈 내 Service 정의
│
│
├─ BuildGradle/                             # 그레이들 모음
│  ├─ build-common.gradle/                  # 공통 dependencies 모음
│  ├─ build-datasource.gradle/              # DB dependencies 모음
│  ├─ build-jwt.gradle/                     # jwt dependencies 모음
│  ├─ build-springboot3.gradle/             # springboot 관련 dependencies 모음
│  └─ build-test.gradle/                    # test 관련 dependencies 모음
│
├─ BuildLibs/                               # component 모듈 jar 생성 폴더
│  ├─ common/                               # component 모듈 중 common jar 생성 폴더
│  └─ todo/                                 # component 모듈 중 todo jar 생성 폴더
│
├─ BuildProperties/                         # YML 파일 위치
│  ├─ dev/                                  # 개발 환경 시 yml 파일
│  └─ local/                                # local 환경 시 yml 파일
│
├─ BuildResource/                           # 빌드 리소스
│  ├─ static/                               # CSS/IMG/JS 등 리소스 
│  └─ templates/                            # thymeleaf HTML 템플릿
│
└─ ProxyMain/                               # Wrapping 모듈 [해당 모듈 명으로 BootJar 생성]
   ├─ build/                                # gradlew clean build bootJar 시 생성
   │  ├─ ... 생략 .../                       # gradlew clean build bootJar 시 생성
   │  └─ libs/                              # 프로젝트 실행 파일 생성 위치 ProxyMain-ver0001.jar
   └─ com.example.choquality.proxy/         # SpringBootApplication 설정 위치

````
2. 프로젝트 구성 
![프로젝트 구성.png](Doc/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8%20%EA%B5%AC%EC%84%B1.png)

## 흐름도(TotalFlowTest) 
1. login test
![login test.png](Doc/login%20test.png)
2. user test
![user test.png](Doc/user%20test.png)
3. todo test
![todo test.png](Doc/todo%20test.png)

## Git clone
````
git clone https://github.com/ChoQuality/Choquality-Spring-Kotlin.git
````
````
cd Choquality-Spring-Kotlin
````
````
cd choquality
````

## 테스트(TotalFlowTest) 실행 방법
``
./gradlew test --tests com.example.choquality.proxy.TotalFlowTest
``
## 실행 방법
``
./gradlew clean build bootJar
``

``
java -jar ./ProxyMain/build/libs/ProxyMain-ver0001.jar
``
## API 설명

### 1. API 명세서 http 파일
[API 명세서.http](Doc/API%20%EB%AA%85%EC%84%B8%EC%84%9C.http) 참조

### 2. API 명세서 내용

#### DB 값 전체 초기화
GET http://localhost:9090/init

#### 유저 signup : 이메일 이름 패스워드로 유저 등록 한다.
POST http://localhost:9090/users/signup
Content-Type: application/json

{
"email":"1@gg.gg"
,"name":"1gg"
,"password":"1234"
}

#### 유저 login : signup 된 유저 이메일과 패스워드로 로그인 한다.
POST http://localhost:9090/users/login
Content-Type: application/json

{
"email":"1@gg.gg"
,"password":"1234"
}

> {% client.global.set("access_token", response.body.access_token); %}

#### 유저 me(GET) : Bearer token이랑 같이 요청 시 해당 유저 정보의 값을 준다.
GET http://localhost:9090/users/me
Authorization: Bearer {{access_token}}

#### 유저 me(PUT) : Bearer token으로 인증된 유저의 이름 혹은 비밀번호를 변경한다. 예제는 이름
PUT http://localhost:9090/users/me
Authorization: Bearer {{access_token}}
Content-Type: application/json

{
"name":"변경이름"
}

#### 유저 me(DELETE) : Bearer token으로 인증된 현재 유저 삭제 한다.
DELETE http://localhost:9090/users/me
Authorization: Bearer {{access_token}}

#### todos(POST) : TODO 등록. 인증된 유저(Bearer토큰)만 등록이 가능하다.
POST http://localhost:9090/todos
Authorization: Bearer {{access_token}}
Content-Type: application/json

{
"title": "title2"
,"content" :"content2"
}

#### todos(GET) : 등록된 TODO 리스트 조회. (자신이 등록한 todo만 조회 가능하다.)
GET http://localhost:9090/todos
Authorization: Bearer {{access_token}}


#### todos id(GET) : 등록된 TODO 단일 조회. (자신이 등록한 todo만 조회 가능하다.)
@todoId=1
GET http://localhost:9090/todos/{{todoId}}
Authorization: Bearer {{access_token}}

#### todos id(PUT) : 등록된 TODO 단일 수정. (자신이 등록한 todo만 수정 가능하다.)
PUT http://localhost:9090/todos/{{todoId}}
Authorization: Bearer {{access_token}}
Content-Type: application/json

{
"title": "title2"
,"content" :"content2"
}

#### todos id(DELETE) : 등록된 TODO 단일 삭제. (자신이 등록한 todo만 수정 가능하다.)
DELETE http://localhost:9090/todos/{{todoId}}
Authorization: Bearer {{access_token}}

#### todos search(GET) : 등록된 TODO 검색. (자신이 등록한 todo만 검색 가능하며, title 혹은 content로 like 검색)
@title=2
GET http://localhost:9090/todos/search?title={{title}}
Authorization: Bearer {{access_token}}

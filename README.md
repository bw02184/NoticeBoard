# NoticeBoard(개인 프로젝트)
링크 : http://noticeboard-env.eba-a3wuwng7.ap-northeast-2.elasticbeanstalk.com/

안녕하세요! NoticeBoard는 저만의 게시판을 만들어 본 프로젝트 입니다. 주요기능으로 게시판 작성기능과 댓글작성 기능이 있습니다. 

프로젝트를 진행하며 좋았던 점은 동영상 강의로 듣던 코드를 직접 생각해 보면서 어떻게 코드를 짜면 좋을지 생각하며 코드를 짤 수 있는 좋은 기회가 되었습니다. 또한 생각만 하던 여러가지 기능을 직접 구현하며 어느정도 까지 구현이 가능한지 확인을 하게 된 점이 좋았던 점입니다.

다만 아쉬웠던 점은 게시판 만들기나 댓글을 만들 때 인터넷 검색을 통해 얻은 정보를 프로젝트에 끼워 맞추다 보니 코드가 조금 지저분하게 된 점이 아쉽습니다. 또한 여러가지 기능을 생각만 해놓고 구현하지 못한점이 아쉬운 점입니다.

때문에 다음 프로젝트는 여유를 가지고 여러가지 기능을 다뤄보려고 합니다. 감사합니다.

### 스킬

 <img src="https://img.shields.io/badge/springboot 2.7.13-6DB33F?style=flat&logo=springboot&logoColor=white"/>  <img src="https://img.shields.io/badge/JPA-blue?style=flat"/> <img src="https://img.shields.io/badge/thymeleaf-black?style=flat&logo=thymeleaf&logoColor=005F0F"/> <img src="https://img.shields.io/badge/Elastic Beanstalk-yellow?style=flat&logo=amazonaws&logoColor=#232F3E"/> <img src="https://img.shields.io/badge/GitHub Actions-red?style=flat&logo=githubactions&logoColor=2088FF"/>

### 구조
~~~
─src
    ├─main
    │  ├─generated
    │  ├─java
    │  │  └─sws
    │  │      └─NoticeBoard
    │  │          ├─controller
    │  │          │  └─form
    │  │          ├─domain
    │  │          ├─interceptor
    │  │          ├─mail
    │  │          ├─repository
    │  │          ├─service
    │  │          └─session
    │  └─resources
    │      ├─static
    │      │  ├─css
    │      │  ├─images
    │      │  └─js
    │      └─templates
    │          ├─board
    │          ├─error
    │          ├─fragments
    │          ├─login
    │          └─member
    └─test
        └─java
            └─sws
                └─NoticeBoard
~~~

### 기간
2023.07.13 ~ 2023.07.23

<H1>기능</H1>

비회원 기능

- 로그인 기능 - `HttpSession` 이용
- 회원 가입 기능 - 비밀 번호 저장 시 `passwordEncoder`  사용
- 아이디 찾기 기능 - email 인증 기능 사용
- 비밀번호 찾기 기능

회원 기능(로그인 기능) - 인터셉터 사용

- 로그아웃 - SpringSecurity 이용
- 회원 정보 변경
- 비밀번호 변경
- 회원 탈퇴하기 - 회원 탈퇴 시 게시판 글은 탈퇴회원으로 변경 처리, 댓글은 삭제 처리

회원기능(게시판 기능)

- 게시판 작성하기 -quill editor 이용
- 게시판 목록 보기 - 페이지네이션 적용
- 게시판 수정하기 - 작성한 사람만 수정 가능
- 게시판 삭제하기 - 작성한 사람만 삭제 가능

회원기능(댓글 기능)

- 댓글 작성하기
- 댓글 리스트 보이기
- 댓글 수정하기 - 작성한 사람만 수정 가능
- 댓글 삭제하기 - 작성한 사람만 수정 가능

### 테스트 계정
1. ID : test  /  PW : test
2. ID : test2 / PW : test2

### ERD

![erd](https://github.com/bw02184/NoticeBoard/assets/102367393/89da8996-71d9-49cc-b03c-491475af86b4)

### 주요 기능 - 게시판 수정
![ezgif-5-c095c6abf8](https://github.com/bw02184/NoticeBoard/assets/102367393/a93ede3b-6d0a-4648-af72-46e8e7f1dbfb)

### CICD(https://github.com/bw02184/git-action-test 참고)
### .github/workflows/eb_deploy.yml
- jar파일을 이용하여 AWS Beanstalk에 빌드 후 배포하는 yml파일이다.
- jobs를 build와 deploy로 나누어 빌드가 완료된 후 배포되도록 작성하였다.
- 캐시를 추가하여 속도가 빠르게 실행되도록 하였으며 AWS 보안그룹 떄문에 RDS가 실행되지 않는 문제점이 발생하여 빌드시에 깃액션의 ip를 보안그룹에 추가 하고 빌드가 완료된 후 무조건(빌드가 실패하더라도) 보안그룹에서 깃 액션의 ip를 다시 제거하는 방식으로 RDS가 작동하도록 작성하였다.
- 관련 노션 링크 : https://responsible-cardigan-ecb.notion.site/Beanstalk-jar-Deploy-cfe18a9a68c54d7fb06ae8883de74e7d

### .github/workflows/eb_docker_deploy.yml
- 도커파일을 이용하여 빌드 후 AWS Beanstalk에 배포하는 yml파일이다.
- 도커파일을 빌드해서 도커허브에 이미지파일을 올려놓으면 AWS상에서 도커허브의 이미지 파일을 이용해서 배포하는 방식이다.
- ./src/main/resources/application.properties 에 중요한 정보(DB id, password 등)들이 많다고 생각되어 깃허브에서 삭제 후 깃 시크릿으르로 저장하여 다운 받는 방식을 사용하였다.
-  Dockerfile과 Dockerrun.aws.json파일이 루트에 있어야 한다.
-  관련 노션 링크 : https://responsible-cardigan-ecb.notion.site/EB-Docker-Deploy-31748ed4210e429d93859d778dd16dfa

### Dockerfile
- 이미지 파일을 나눠 받는 방식을 사용하여 속도를 빠르게 했다.
- MAC OS와 Windows OS의 개행 방식 차이에 따라 개행문자 변경해 주는 dos2unix설치 후 gradlew 변경해 주는 과정을 더해 주었다.(windows 환경이기 때문에 추가했다.)

### 프로젝트 노션 링크
- https://responsible-cardigan-ecb.notion.site/NoticeBoard-e17c778ad569497083cd92ace2e3ba59?pvs=4

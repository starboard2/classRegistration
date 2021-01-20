# classRegistration


수강 신청

본 예제는 MSA/DDD/Event Storming/EDA 를 포괄하는 분석/설계/구현/운영 전단계를 커버하도록 구성한 예제입니다.
이는 클라우드 네이티브 애플리케이션의 개발에 요구되는 체크포인트들을 통과하기 위한 예시 답안을 포함합니다.
- 체크포인트 : https://workflowy.com/s/assessment-check-po/T5YrzcMewfo4J6LW


# Table of contents

- [예제 - 밀키트판매](#---)
  - [서비스 시나리오](#서비스-시나리오)
  - [체크포인트](#체크포인트)
  - [분석/설계](#분석설계)
  - [구현:](#구현-)
     1. SAGA
     2. CQRS
     3. Correlation
     4. Req/Res
  - [운영](#운영)
  
     5. Gateway
     6. Deploy
     7. CB
     8. HPA
     9. Readiness


# 서비스 시나리오


기능적 요구사항

1. 교수가 강의를 등록한다.
2. 학생이 강의를 선택하여 수강 신청한다.
3. 수강신청이 완료되면 수강 가능인원을 변경을 한다.
4. 학생이 수강 신청을 취소할 수 있다.
5. 수강 신청이 취소되면 등록이 취소된다.
6. 학생이 수강내역을 중간중간 조회할 수 있다.

비기능적 요구사항
1. 트랜잭션
    1. 수강 신청 등록완료가 되지 않은 건은 신청 완료되지 않아야 한다  Sync 호출
    
1. 장애격리
    1. 강의등록 기능이 수행되지 않더라도 수강 신청은 365일 24시간 받을 수 있어야 한다  Async (event-driven), Eventual Consistency
    1. 등록이 과중되면 사용자를 잠시동안 받지 않고 등록을 잠시후에 하도록 유도한다  Circuit breaker, fallback
1. 성능
    1. 학생이 수시로 등록 상태를 마이페이지에서 확인할 수 있어야 한다  CQRS




## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과:  


### 완성된 1차 모형
![image](https://user-images.githubusercontent.com/75401961/105137047-0e3a5e80-5b36-11eb-9daf-ff450041fbc8.png)

    - View Model 추가

### 1차 완성본에 대한 기능적/비기능적 요구사항을 커버하는지 검증

![image](https://user-images.githubusercontent.com/75401961/105137370-84d75c00-5b36-11eb-83c4-a867ab1b15cc.png)

    - 고객이 상품을 선택하여 주문한다 (ok)
    - 주문하면 결제가 동시에 이뤄진다. (ok)
    - 결제가 이뤄지면 배송이 요청된다. (ok)
    - 결제가 이뤄지면 상품재고가 감소한다. (ok)
    - 배송이 요청되고 배송이 시작되면 주문상태가 배송시작으로 변경된다. (ok)

![image](https://user-images.githubusercontent.com/75401961/105137421-9ae51c80-5b36-11eb-8dd4-ff8e51066adf.png)

    - 고객이 주문을 취소할 수 있다 (ok)
    - 주문이 취소되면 결제가 취소된다 (ok)
    - 결제가 취소되면 배송이 취소된다. (ok)
    - 배송이 취소되면 주문상태가 배송취소로 변경된다. (ok)




### 비기능 요구사항에 대한 검증

![image](https://user-images.githubusercontent.com/75401961/105137793-3ffff500-5b37-11eb-965e-b4992b7e5fa1.png)

    - 마이크로 서비스를 넘나드는 시나리오에 대한 트랜잭션 처리
        - 고객 주문시 결제처리:  결제가 완료되지 않은 주문은 절대 받지 않는다는 경영자의 오랜 신념(?) 에 따라, ACID 트랜잭션 적용. 주문완료시 결제처리에 대해서는 Request-Response 방식 처리
        - 결제완료 시 이벤트 드리븐 방식으로 상품서비스의 상태에 관계없이 처리됨
        - 결제시스템이 과부하 걸릴 경우 잠시동안 결제를 못하도록 유도함
        - 고객은 수시로 마이페이지에서 조회 가능함





# 구현:

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 


1,3. 주문->결제->배송->주문 캡쳐

등록(subject)
![image](https://user-images.githubusercontent.com/75401961/105138224-f19f2600-5b37-11eb-970c-aa55eb18f3f5.png)

등록(stdent)
![image](https://user-images.githubusercontent.com/75401961/105142752-4e054400-5b3e-11eb-99eb-df3ed4f757ea.png)

student
![image](https://user-images.githubusercontent.com/75401961/105143181-d4218a80-5b3e-11eb-91e1-22f385e583db.png)

registration
![image](https://user-images.githubusercontent.com/75401961/105143293-fe734800-5b3e-11eb-8ab7-9740dffa9d71.png)

mypage
![image](https://user-images.githubusercontent.com/75401961/105143415-29f63280-5b3f-11eb-926f-dc225003d600.png)

Subject
![image](https://user-images.githubusercontent.com/75401961/105143719-8d806000-5b3f-11eb-8c8d-9bfb21d22d38.png)


취소(Student)
![image](https://user-images.githubusercontent.com/75401961/105143888-cb7d8400-5b3f-11eb-902b-197796845ad3.png)

student
![image](https://user-images.githubusercontent.com/75401961/105144013-f8319b80-5b3f-11eb-8c5d-9a2b2f197237.png)

registration
![image](https://user-images.githubusercontent.com/75401961/105144072-0aabd500-5b40-11eb-9b63-c2fd78154769.png)

mypage
![image](https://user-images.githubusercontent.com/75401961/105144115-18f9f100-5b40-11eb-984b-93f20e539582.png)

Subject
![image](https://user-images.githubusercontent.com/75401961/105144211-3e86fa80-5b40-11eb-8d26-edc79700d5cc.png)

Registration 중지
![image](https://user-images.githubusercontent.com/75401961/105144779-18158f00-5b41-11eb-9f86-8bf0d23439aa.png)

Registration 서비스 재기동
![image](https://user-images.githubusercontent.com/75401961/105144966-5e6aee00-5b41-11eb-8184-54c5cb3d7c5c.png)






 - 주문 등록 후 주문내역 조회 시 배송상태 변경됨 


 - 주문 등록 후 결제 내역 조회


 - 재고변경


 - 주문취소


 - 주문취소 시 결제 취소 반영됨


 - 결제취소시 배송 취소
 


2. 마이페이지 조회



3. 결제서비스 장애 시 주문 불가

   

5.6 Gateway, Deploy

product 상품 등록 
 - LoadBalanced로 노출된 퍼블릭IP로 상품등록 API 호출

![image](https://user-images.githubusercontent.com/75401920/105001534-42008000-5a73-11eb-8ab7-c955745e7703.png)


애져에 배포되어 있는 상황 조회 kubectl get all

![image](https://user-images.githubusercontent.com/75401920/105000728-06b18180-5a72-11eb-8609-e527c48f7060.png)



7. Istio 적용 캡쳐

  - Istio테스트를 위해서 Payment에 sleep 추가
  
![image](https://user-images.githubusercontent.com/75401920/105005616-e89b4f80-5a78-11eb-82cb-de53e5881e3f.png)

 - istio Virtual Service 생성

![image](https://user-images.githubusercontent.com/75401920/105109571-22fbff80-5b00-11eb-9690-74b751e435a6.png)

![image](https://user-images.githubusercontent.com/75401920/105109657-5179da80-5b00-11eb-9e87-637a565c75ad.png)

 - 적용 후 siege로 부하 테스트
  3초 넘어가는 요청건은 실패로 처리 됨

![image](https://user-images.githubusercontent.com/75401920/105109994-07452900-5b01-11eb-857b-385a5960fecb.png)

 추가 적용
 - payments 서비스에 Istio 적용
   
![image](https://user-images.githubusercontent.com/75401920/105006822-7f1c4080-5a7a-11eb-9191-db35233773d3.png)

 - Istio 적용 후 seige 실행 시 대략 50%정도 확률로 CB가 열려서 처리됨

![image](https://user-images.githubusercontent.com/75401920/105006958-b2f76600-5a7a-11eb-99f3-c8b81a4ec270.png)

8. AutoScale

   
 - AutoScale 적용된 모습

   kubectl autoscale deployment order --cpu-percent=10 --min=1 --max=10

![image](https://user-images.githubusercontent.com/75401961/105156323-063ae880-5b4f-11eb-9db9-1de6b8eab9c7.png)
![image](https://user-images.githubusercontent.com/75401961/105174161-7b191d00-5b65-11eb-9f2b-dd00bb92fc12.png)

 - AutoScale적용 후 seige를 통해서 부하 테스트 시  order pod 개수가 증가함




9. Readness Proobe



   siege로 계속 호출하는 중에 kubectl set image를 통해서 배포 시 무중단 배포 확인
 
  - Readiness 적용 전: 소스배포시 500 오류 발생
  
![image](https://user-images.githubusercontent.com/75401961/105190041-f20be100-5b78-11eb-837b-aa6fd6fc1868.png)
![image](https://user-images.githubusercontent.com/75401961/105190107-05b74780-5b79-11eb-9cbd-a02866a2db7f.png)

  - 적용 후: 소스배포시 100% 수행됨
  
![image](https://user-images.githubusercontent.com/75401961/105190150-123ba000-5b79-11eb-8212-94693df6e340.png)
![image](https://user-images.githubusercontent.com/75401961/105190241-241d4300-5b79-11eb-8a95-f811fa0e0ab3.png)

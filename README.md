# classRegistration

- 체크포인트 : https://workflowy.com/s/assessment-check-po/T5YrzcMewfo4J6LW

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
    1.1. 수강 신청 등록완료가 되지 않은 건은 신청 완료되지 않아야 한다 (Req/Res)
2. 장애격리
    2.1. 강의등록 기능이 수행되지 않더라도 수강 신청은 365일 24시간 받을 수 있어야 한다
    2.2. 등록이 과중되면 사용자를 잠시동안 받지 않고 등록을 잠시후에 하도록 유도한다
3. 성능
    3.3. 학생이 수시로 등록 상태를 마이페이지에서 확인할 수 있어야 한다 (CQRS)




## Event Storming 결과
![image](https://user-images.githubusercontent.com/75401961/105137047-0e3a5e80-5b36-11eb-9daf-ff450041fbc8.png)

### 기능적/비기능적 요구사항 검증

![image](https://user-images.githubusercontent.com/75401961/105137370-84d75c00-5b36-11eb-83c4-a867ab1b15cc.png)

    - 학생이 과목을 선택하여 수강 신청한다 (ok)
    - 수강 신청이 완료되면 수강 등록이 이뤄진다. (ok)
    - 수강 신청이 등록되면 수강 가능인원이 감소한다. (ok)
    - 수강 신청이 등록되면 학생의 수강 등록상태가 변경된다. (ok)

![image](https://user-images.githubusercontent.com/75401961/105137421-9ae51c80-5b36-11eb-8dd4-ff8e51066adf.png)

    - 학생이 수강을 취소할 수 있다 (ok)
    - 수강 신청이 취소되면 수강 가능인원이 증가한다. (ok)
    - 수강 신청이 취소되면 학생의 수강 등록상태가 변경된다. (ok)



# 구현:

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 

#### 강의 등록(subject)
![image](https://user-images.githubusercontent.com/75401961/105138224-f19f2600-5b37-11eb-970c-aa55eb18f3f5.png)

#### 수강 신청(stdent)
![image](https://user-images.githubusercontent.com/75401961/105142752-4e054400-5b3e-11eb-99eb-df3ed4f757ea.png)

#### 수강 신청 조회(student)
![image](https://user-images.githubusercontent.com/75401961/105143181-d4218a80-5b3e-11eb-91e1-22f385e583db.png)

#### 등록 조회(registration)
![image](https://user-images.githubusercontent.com/75401961/105143293-fe734800-5b3e-11eb-8ab7-9740dffa9d71.png)

#### 마이페이지 조회(mypage)
![image](https://user-images.githubusercontent.com/75401961/105143415-29f63280-5b3f-11eb-926f-dc225003d600.png)

#### 과목의 신청가능 인원 감소(Subject)
![image](https://user-images.githubusercontent.com/75401961/105143719-8d806000-5b3f-11eb-8c8d-9bfb21d22d38.png)

# 

#### 수강 취소(Student)
![image](https://user-images.githubusercontent.com/75401961/105143888-cb7d8400-5b3f-11eb-902b-197796845ad3.png)

#### 수강 취소 조회(student)
![image](https://user-images.githubusercontent.com/75401961/105144013-f8319b80-5b3f-11eb-8c5d-9a2b2f197237.png)

#### 등록 취소 조회(registration)
![image](https://user-images.githubusercontent.com/75401961/105144072-0aabd500-5b40-11eb-9b63-c2fd78154769.png)

#### 마이페이지 조회(mypage)
![image](https://user-images.githubusercontent.com/75401961/105144115-18f9f100-5b40-11eb-984b-93f20e539582.png)

#### 과목의 신청가능 인원 증가(Subject)
![image](https://user-images.githubusercontent.com/75401961/105144211-3e86fa80-5b40-11eb-8d26-edc79700d5cc.png)

#

#### 등록 서비스 중지(Registration)
![image](https://user-images.githubusercontent.com/75401961/105144779-18158f00-5b41-11eb-9f86-8bf0d23439aa.png)

#### 등록 서비스 재기동(Registration)
![image](https://user-images.githubusercontent.com/75401961/105144966-5e6aee00-5b41-11eb-8184-54c5cb3d7c5c.png)


### 5.6 Gateway, Deploy

 - LoadBalanced로 노출된 퍼블릭IP로 API 호출



### 7. Circuit Breaker

 - timeout
 - ConnectionPool / OutlierDetection




### 8. AutoScale
   
 - AutoScale 적용된 모습

     - kubectl autoscale deployment order --cpu-percent=10 --min=1 --max=10
    
    - kubectl exec -it siege -c siege -- /bin/bash
    - siege -c20 -t50S -v --content-type "application/json" 'http://student:8080/students POST {"studnetName": "Kim", "classId":1}'
 
 
 - AutoScale적용 후 seige를 통해서 부하 테스트 시  student pod 개수가 증가함

![image](https://user-images.githubusercontent.com/75401961/105174161-7b191d00-5b65-11eb-9f2b-dd00bb92fc12.png)



### 9. Readness Proobe

 - 부하테스트 툴(Siege)로 계속 호출하는 중에 kubectl set image를 통해서 배포 시 무중단 배포 확인
    - kubectl exec -it siege -c siege -- /bin/bash
    - siege -c20 -t50S -v --content-type "application/json" 'http://52.141.61.243:8080/subjects POST {"className": "Math", "maximumStudent":50}'
    - kubectl set image deploy subject subject=skcc19.azurecr.io/subject:v3
   
   
 - Readiness 적용 전: 소스 배포시 500 오류 발생
  
![image](https://user-images.githubusercontent.com/75401961/105190860-cccba280-5b79-11eb-9696-50dd8f1513b0.png)
![image](https://user-images.githubusercontent.com/75401961/105190041-f20be100-5b78-11eb-837b-aa6fd6fc1868.png)
![image](https://user-images.githubusercontent.com/75401961/105190107-05b74780-5b79-11eb-9cbd-a02866a2db7f.png)

  - 적용 후: 소스배포시 100% 수행됨
  
![image](https://user-images.githubusercontent.com/75401961/105190771-b02f6a80-5b79-11eb-8e1c-6733d6a80f4d.png)
![image](https://user-images.githubusercontent.com/75401961/105190150-123ba000-5b79-11eb-8212-94693df6e340.png)
![image](https://user-images.githubusercontent.com/75401961/105190334-4020e480-5b79-11eb-99c6-29cc2fd4a529.png)

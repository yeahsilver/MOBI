# 🏃 모두의 헬스 케어 비서 - MOBI 
> Notion: https://www.notion.so/MOBI-041d60cbe6864780a47d9ba9e671b8f8

<img width="300" alt="스크린샷 2020-06-14 오후 11 02 32" src="https://user-images.githubusercontent.com/39258902/84595492-2442bb00-ae93-11ea-9322-eef38997bd5c.png">

## ✍️ 주요 기능

* **식이 관리**

  * 영양 성분표 기반으로 탄수화물, 지방, 단백질 등의 함유량을 계산 및 몸 상태에 맞는 수치 업데이트

* **몸 상태 & 관리 루틴 제시**

  * BMI 지수, 근육량, 체지방량 기록 후 수치 예측


<br/>

## 👩‍👩‍👧‍👧 About Project

> 숭실대학교 소프트웨어학부 2020-1 소프트웨어 프로젝트
<br/>

## 💁 팀 구성
  * **김승지: Project Manager & Design Administrator**

    * ` UI `, ` Firebase ML KIT Manager `, ` Firebase Database Manager `

  * **박재희: Design Administrator**

    * ` UI `, ` Firebase Database Manager `

  * **허예은: firebase Connect Manager**

    * ` Firebase ML KIT Connect Manager `, `Schedule Manager`

  * **박정아: Server Manager & Database Administrator**

    * ` Firebase Database Manager `, ` Schedule Manager `

<br/>

## ⏰   프로젝트 일정
![플래너](https://user-images.githubusercontent.com/39258902/85100569-6aa16c80-b23b-11ea-9329-ace3e9067284.png)

<br/>


## 👩🏻‍💻  개발 환경
* Android Studio

* Firebase Database

* Firebase Authentication (oauth)

* Firebase ML KIT - Text Recognition


<br/>

## ✏️  브랜치 전략
> Git Branch 전략

  * ` master `

    * 서버 연동 브랜치
    * 회의록 업로드 브랜치

  * ` dev `

    * new content 업로드 브랜치. pull request를 생성 후 통과된 폴더 및 파일만 'master'에 merge

 *  ` 팀원명 `

    * 새로운 기능을 추가할 때 생성하는 브랜치. 기능이 완성되면 'dev' 브랜치에 merge 후 제거 (현재 모든 브랜치 제거 완료)

<br/>

> Commit Chart
>  > 2020년 6월 25일자로 모든 브랜치 관리 완료.
<img width="780" alt="스크린샷 2020-06-26 오전 12 04 32" src="https://user-images.githubusercontent.com/39258902/85745386-9f805880-b740-11ea-87b8-70a48d78766a.png">


<br/>

## ⚒   Project Architecture

> Service Architecture
<img width="1335" alt="스크린샷 2020-06-14 오후 10 49 21" src="https://user-images.githubusercontent.com/39258902/84595126-505d3c80-ae91-11ea-968e-e9fa0ca5748f.png">


> DB Architecture

![Database Shcema-4 (1)](https://user-images.githubusercontent.com/28800101/82138942-4ed33100-985f-11ea-837e-d806b386469d.png)

* **User**
  * 사용자의 아이디와 비밀번호, 해당 사용자 데이터가 가지고 있는 하위 정보
* **RecDailyIntake**
  * 사용자의 정보를 바탕으로 사용자가 섭취해야 하는 영양성분의 총량
* **DailyIntake**
  * 스캔한 영양 성분표에 기재된 정보, 섭취한 성분의 숫자 데이터 값
* **TotalDailyIntake**
  * 스캔한 영양 성분표를 기준으로 한 사용자가 실제 섭취한 영양 성분의 총량

<br/>

> Firebase Authentication
  - ` User Info `

> Firebase Cloud
  - ` ML Kit Module `

<br/>

 ## 🗞  Reference Lists
  - **개발 환경**
    - [Firebase Documentation](https://firebase.google.com/docs/ )
    - [Firebase ML kit - Text Recognition (Android Version)](https://firebase.google.com/docs/ml-kit/android/recognize-text)
    - [Firebase Database Read and Write](https://firebase.google.com/docs/database/android/read-and-write?hl=ko )
    - [Firebase Authentification](https://firebase.google.com/docs/auth/android/firebaseui )
    - [Firebase Text Recognition Test Github - yeeun]( https://github.com/yeahsilver/hText-Recognition-Test)
    - [Google Cloud STT Test version Github - seungji]( https://github.com/seungjikim/Speech_to_text )
    - [스캔 이미지 처리](https://www.opentutorials.org/module/3811/25283)
    - [Tess two를 이용한 ocr 앱 만들기 문자 인식](https://hjiee.tistory.com/entry/Android-TessTwo를-이용한-OCR-앱-만들기문자인식)
    - [Tenserflow를 이용한 object recognition](https://cloud.google.com/solutions/creating-object-detection-application-tensorflow?hl=ko)
    - [Tesseract 개발환경 만들기](https://junyoung-jamong.github.io/computer/vision/2019/02/07/Android-Tesseract-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0.html)
    - [Camera X](https://developer.android.com/training/camerax)
    
  - **오픈소스**
    - [Android Chart](https://github.com/PhilJay/MPAndroidChart)



<br/>  

## 📑  회의록
- [2020년 3월 15일 월요일 회의록](./meeting-record/20200315.md )
- [2020년 3월 22일 일요일 회의록](./meeting-record/20200322.md)
- [2020년 3월 30일 화요일 회의록](./meeting-record/20200331.md )
- [2020년 4월 6일 월요일 회의록](./meeting-record/20200408.md)
- [2020년 4월 27일 월요일 회의록](./meeting-record/20200427.md)
- [2020년 5월 4일 월요일 회의록](./meeting-record/20200504.md)
- [2020년 5월 13일 월요일 회의록](./meeting-record/20200513.md)
- [2020년 5월 18일 월요일 회의록](./meeting-record/20200518.md)
- [2020년 5월 25일 월요일 회의록](./meeting-record/20200525.md)
- [2020년 6월 1일 월요일 회의록](./meeting-record/20200601.md)
- [2020년 6월 8일 월요일 회의록](./meeting-record/20200608.md)

## 💻 프로젝트 요약
<img width="1790" alt="스크린샷 2020-06-14 오후 10 46 12" src="https://user-images.githubusercontent.com/39258902/84595053-dfb62000-ae90-11ea-87d8-5a27adc8239f.png">


<img width="1788" alt="스크린샷 2020-06-14 오후 10 48 37" src="https://user-images.githubusercontent.com/39258902/84595107-34599b00-ae91-11ea-84a4-04c0fb288068.png">


<img width="1792" alt="스크린샷 2020-06-14 오후 10 52 21" src="https://user-images.githubusercontent.com/39258902/84595249-b77af100-ae91-11ea-99cc-0184fb1c8657.png">


<img width="1792" alt="스크린샷 2020-06-14 오후 10 53 50" src="https://user-images.githubusercontent.com/39258902/84595274-eb561680-ae91-11ea-8e14-541dad6d9bae.png">


<img width="1791" alt="스크린샷 2020-06-14 오후 10 54 04" src="https://user-images.githubusercontent.com/39258902/84595279-f3ae5180-ae91-11ea-85a9-0f681421991d.png">


<img width="1789" alt="스크린샷 2020-06-14 오후 10 54 19" src="https://user-images.githubusercontent.com/39258902/84595284-fc9f2300-ae91-11ea-8957-e144a49636dd.png">


<img width="1791" alt="스크린샷 2020-06-26 오전 12 10 00" src="https://user-images.githubusercontent.com/39258902/85746382-61cfff80-b741-11ea-8ebf-af75eea393ef.png">


<img width="1792" alt="스크린샷 2020-06-14 오후 10 56 07" src="https://user-images.githubusercontent.com/39258902/84595323-3cfea100-ae92-11ea-98c8-2ed6ec2b2755.png">

# 🚧 Back-End
## 커밋 관련
### 커밋 이름
- `Feat` : 새로운 기능 추가
- `Add` : 파일 추가
- `Del` : 파일 삭제
- `Fix` : 버그 수정
- `Docs` : 문서 수정
- `Style` : 스타일 변경, semicolon 누락, 코드 변경이 없는 경우
- `Test` : 테스트 코드, refactoring 테스트 코드 추가
- `Chore` : 빌드 수정, package 수정, 기타
- `Refactor` : 코드 리팩토링
### 커밋 규칙
- 기본적으로 `Feat: OOOOO` 이런 방식으로 표기 첫 글자는 대문자, : 뒤에만 공백이 있음에 유의
- 제목은 최대 20자를 넘지 않는다.
- 설명이 필요한 경우 제목에서 1행 공백을 주고 내용을 작성한다.

## 명명 규칙
### Github
- `Github Branch` : commit 태그-issue번호 ex)feat-1, refactor-2
- `Issue` : OO페이지 OO기능 구현
- `Pull Request` : Title of last commit
### BackEnd
- `Method` : camelCase
- `Entity` : lowercase
- `Table` : lowercase
- `Column` : snake_case
### FrontEnd
- `Function` : camelCase
- `Hook` : camelCase
- `Styled Components` : PascalCase
- `Custom Hook File Name` : kebab-case
### Common(공통)
- `Class` : PascalCase
- `Assets` : snake_case
- `Other Variables` : camelCase
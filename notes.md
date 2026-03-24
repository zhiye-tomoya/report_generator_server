```
docker compose up -d
./gradlew test --tests "com.example.reportGenerator.ReportGeneratorApplicationTests.contextLoads"
git log --oneline -1
git commit --amend -m "feat: implement email normalization"
git log -1 --pretty=format:"%h %s%n%n%b"
```

## TODO:

- Look into UserRepositoryTest.kt
- Create report entity
  - Column
    - title
    - notes

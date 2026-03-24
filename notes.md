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

## 実装計画（TDDアプローチ）

### 仕様

- **エンドポイント**: `GET /api/reports/search?from=YYYY-MM-DD&to=YYYY-MM-DD`

- **パラメータ**:
  - `from`: 開始日（省略可）→ その日の00:00:00以降
  - `to`: 終了日（省略可）→ その日の23:59:59.999999999以前
  - 両方省略 → 全レポート取得

- **フォーマット例**: `2026-03-24`

### TDD実装ステップ

#### 1. **テストファースト: Repository層**

- `ReportRepositoryTest.kt`に統合テストを追加
- 日付範囲での検索メソッドのテストケース作成
- テストを実行（RED）

#### 2. **Repository層の実装**

- `ReportRepository.kt`にSpring Data JPAのクエリメソッド追加
- `findByCreatedAtBetween`メソッドを実装
- テストを実行（GREEN）

#### 3. **テストファースト: Service層**

- `ReportServiceTest.kt`に単体テストを追加
- fromのみ、toのみ、両方、なしの4パターンをテスト
- 日付をLocalDateTimeに変換するロジックをテスト
- テストを実行（RED）

#### 4. **Service層の実装**

- `ReportService.kt`に`searchReportsByDateRange`メソッド追加
- LocalDate → LocalDateTime変換ロジック実装
- テストを実行（GREEN）

#### 5. **テストファースト: Controller層**

- `ReportControllerTest.kt`に単体テストを追加
- クエリパラメータのパース処理をテスト
- 各パターンのレスポンスをテスト
- テストを実行（RED）

#### 6. **Controller層の実装**

- `ReportController.kt`に`searchReports`エンドポイント追加
- `@RequestParam`で日付文字列を受け取る
- テストを実行（GREEN）

#### 7. **リファクタリング**

- コードの整理とクリーンアップ
- エラーハンドリングの追加（不正な日付フォーマット等）

### 実装詳細

**主要なロジック**:

- `from`日付 → `LocalDate.atStartOfDay()` (00:00:00)
- `to`日付 → `LocalDate.atTime(23, 59, 59, 999999999)` (23:59:59.999999999)
- パラメータに応じてクエリを動的に変更

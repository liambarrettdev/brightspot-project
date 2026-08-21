# AGENTS.md

Guidance for AI coding agents working in this repository.

## Project

Brightspot CMS 3.x web application (Dari framework) built as a WAR. Java 8 only —
`maven-compiler-plugin` is pinned to source/target 1.8 and the Docker image is `jdk8`.
`.java-version` is `1.8`; do not introduce Java 9+ APIs or syntax.

## Commands

```bash
# Full build (compiles, generates view classes, runs npm/grunt, runs Dari code-style checks)
npm run build          # == mvn clean -P library verify

# Bring up the local stack (Tomcat, MySQL, Solr, Apache/dims, GreenMail)
npm run docker         # == docker compose up

# Frontend: styleguide dev server on http://localhost:3000
npm run styleguide     # == sh styleguide.sh (patches bsp-styleguide, then runs its bin)

# Frontend: rebuild LESS/JS on change (separate terminal)
npm run watch          # == grunt watch
```

The `-P library` profile is what runs the code-style checker; always build with it.
CI (`.github/workflows/maven.yml`) runs exactly `mvn clean -P library verify` on JDK 8.

There is **no test suite** — no `src/test`, no test dependencies. "Verification" for a
change means the Maven build passes and, for frontend work, the styleguide renders.

Local URLs: app at `http://localhost` (Apache) / `:8080` (Tomcat), CMS at `/cms`,
remote debug on port 5005, Solr on `:8081`, MySQL on `:3306`.
Local Dari settings live in `docker-context.properties` (mounted into the Tomcat container).

## Architecture: the four-file pattern

Rendering is styleguide-driven. A renderable thing is spread across four locations that
must stay in sync, and the *JSON is the contract*:

1. `styleguide/**/Foo.json` — the view definition. Maven's `exec-maven-plugin`
   (`generate-view-classes`, phase `process-resources`) runs
   `com.psddev.styleguide.ViewClassGenerator` over the `styleguide/` dir and generates
   `com.brightspot.view.**.FooView` interfaces into
   `target/generated-sources/styleguide` (added to the compile path by
   `build-helper-maven-plugin`). **Never edit generated `com.brightspot.view.*` classes** —
   change the JSON and rebuild.
2. `src/main/webapp/**/Foo.hbs` — the Handlebars template, plus `Foo.less` for styles.
   `webapp/model/**` mirrors `styleguide/model/**` path-for-path.
3. `src/main/java/com/brightspot/model/**/Foo.java` — the Dari content model
   (extends `Content` / `AbstractPage` / `AbstractModule`), with CMS field annotations
   (`@ToolUi.*`, `@Recordable.Required`, `@Recordable.Embedded`).
4. `src/main/java/com/brightspot/model/**/FooViewModel.java` — extends
   `AbstractViewModel<Foo>` and implements the generated `FooView`; maps model → view.

Wiring: the model carries `@ViewBinding(value = FooViewModel.class, types = { ... })`.
Page-level content binds to `PageViewModel.MAIN_CONTENT_VIEW`. See
`model/article/Article.java` + `ArticlePageViewModel.java` + `styleguide/model/article/ArticlePage.json`
+ `webapp/model/article/ArticlePage.hbs` as the canonical example.

`AbstractViewModel` (`model/AbstractViewModel.java`) is the base for all ViewModels and
supplies request-scoped injection (`@MainObject`, `@CurrentSite`, `@CurrentUser`,
`@HttpServletPath`, `@HttpParameter`) plus helpers you should reuse rather than
reimplement: `buildObjectView`, `buildLinkView`, `buildListModuleView`,
`buildModuleViews`, `buildRichTextView`, `buildConcatenatedView`.

## Other structural notes

- **Framework patches**: `src/main/java/com/psddev/**` (`dari/util/RepeatingTask`,
  `dari/util/MailMessage`, `dari/db/ObjectField`, `cms/view/ViewModel`) are deliberate
  source-level overrides of upstream Dari/Brightspot classes that shadow the jar
  versions. Treat them as patches — keep them minimal and expect upgrade friction.
- **Servlets** are routed by Dari, not `web.xml`: annotate with
  `@RoutingFilter.Path(...)` and expose the path as a `SERVLET_PATH` constant
  (see `servlet/LoginServlet.java`). `web.xml` only wires `ApplicationFilter`,
  `PageFilter`, and the JSP servlet.
- **Auth** is custom (the upstream `auth` plugin is commented out in `pom.xml`):
  `auth/` provides `AbstractAuthenticator`, `AuthenticationFilter`, `Session`, `Token`,
  with per-site config in `AuthenticationSettings`. See `docs/guides/AUTHENTICATION_GUIDE.md`.
- **Background work**: `task/` has `AbstractTask` / `AbstractCronTask` /
  `MutuallyExclusiveTask` / `TriggerableTask` with settings objects; CMS-side controls in
  `tool/task/` and `tool/recalculation/`.
- **CMS customization** lives in `tool/` (`CustomTool`, `CustomGlobalSettings`,
  `CustomSiteSettings`, custom fields under `tool/field/`, widgets, RTE processors,
  reports — see `docs/guides/REPORTS_GUIDE.md`).
- **Shared logic** belongs in `utils/` (`Utils`, `ContentUtils`, `RichTextUtils`,
  `DirectoryUtils`, `LocalizationUtils`, …) — check there before writing a helper.

## Conventions

- **Exceptions**: use the `com.brightspot.exception` hierarchy —
  `BrightspotException` with `ConfigurationException`, `DatabaseOperationException`,
  `EncryptionException`, `ValidationException`. Catch specific types, never bare
  `Exception`. Full rules in `docs/standards/EXCEPTION_HANDLING_GUIDELINES.md`.
- **Logging**: SLF4J, `private static final Logger LOGGER = LoggerFactory.getLogger(X.class);`
  Abstract bases expose `protected abstract Logger logger()` instead. Use parameterized
  messages. Full rules in `docs/standards/LOGGING_STANDARDS.md`.
- **Java style**: 4-space indent; heavy use of `Optional` chains for null-safe mapping;
  section comments (`// -- Overrides -- //`, `// -- Helper Methods -- //`) separate
  overridden methods from helpers. Prefer `org.apache.commons.lang3`.
- **Frontend**: LESS with BEM; Handlebars templates use `defineBlock` / `defineElement` /
  `defineBlockContainer` and `extend=` for inheritance.
- **Git**: PRs target `develop`; `main` is the release branch.

## Gotchas

- `bower_components/` and `node_modules/` are checked in — ignore them when searching.
- The frontend toolchain is pinned to old versions (Node v14.21.3 / npm 6.14.18 installed
  by `frontend-maven-plugin`, `npm install --legacy-bundling`, Bower, Grunt). Don't
  "modernize" it casually; `styleguide.sh` exists purely to `sed`-patch a deprecated
  `tmpDir` call in `bsp-styleguide`.
- Brightspot/Dari dependency versions are hash-suffixed builds from
  `https://artifactory.psdops.com/public`; they are not on Maven Central.

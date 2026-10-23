# Release

The SDK is published as `ai.pinata:pinata-ui` to `https://static.pinata.ai/static/android/maven`.

1. Bump `sdkVersion` in `gradle.properties` (semver) in its own PR and merge it.
2. Tag the merge commit on `main` and push the tag:

   ```sh
   git tag v<sdkVersion>
   git push origin v<sdkVersion>
   ```

3. The `Publish` workflow runs the tests, builds the release AAR and uploads it. It fails if the tag does not match `sdkVersion` or if that version is already published (published versions never change).

The workflow needs the repo secrets `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_DEFAULT_REGION` and `DISTRIBUTION_ID`, the same ones `pinata-ui` uses to deploy its bundle.

# Environment

## Project Information

- `PROJECT_NAME`: Used as the project name.

## Publishing

These environment variables are used in the `POM` file:

- `GIT_USER_NAME`: Git user name.
- `GIT_ORGANIZATION_NAME`: Organization name.
- `GIT_REPOSITORY_NAME`: Repository name.

These environment variables are used for authentication.  
The system checks for the user token first. If not found, it checks the repository token, and if still not found, it checks the organization token.

- `GIT_USER_TOKEN`: Personal token. This may require a role in the organization such as `owner` or `co-op`, or specific permissions granted by the organization.
- `GIT_ORGANIZATION_TOKEN`: Organization token. Typically held by higher roles and can be used across multiple repositories assigned to or owned by the organization.
- `GIT_REPOSITORY_TOKEN`: Repository-specific token, used only for this repository.

## Warning

Default values have already been set, so none of the above variables are required unless you are publishing.

package botobo.core.infrastructure;

import botobo.core.domain.user.SocialType;
import botobo.core.domain.user.User;
import botobo.core.dto.auth.OauthTokenResponse;

public interface OauthManager {
    User getUserInfo(String code);

    boolean isSameSocialType(SocialType socialType);

    OauthTokenResponse getAccessToken(String code);
}

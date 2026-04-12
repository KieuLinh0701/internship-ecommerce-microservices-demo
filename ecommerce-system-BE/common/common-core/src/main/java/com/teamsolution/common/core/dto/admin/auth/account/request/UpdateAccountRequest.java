package com.teamsolution.common.core.dto.admin.auth.account.request;

import com.teamsolution.common.core.enums.auth.AccountStatus;

public record UpdateAccountRequest(AccountStatus status) {}

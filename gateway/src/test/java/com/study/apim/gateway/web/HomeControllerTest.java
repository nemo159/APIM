package com.study.apim.gateway.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HomeControllerTest {

    @Test
    void redirectsRootToMemberLogin() {
        HomeController controller = new HomeController();

        assertThat(controller.home()).isEqualTo("redirect:/member/login");
    }
}

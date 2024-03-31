package edu.java;

import edu.java.dtos.ListLinksResponse;
import edu.java.services.LinkService;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
public class RateLimitTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LinkService linkService;

    @Value("${bucket4j.filters[0].rate-limits[0].bandwidths[0].capacity}")
    private Integer rateLimit;

    @Test
    public void assertThatRateLimiterWorksRight() throws Exception {
        final Long tgChatId = 1L;
        when(linkService.findAllLinks(any())).thenReturn(new ListLinksResponse(new ArrayList<>(),0));
        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/links")
            .header("Tg-Chat-Id", tgChatId);

        for (int i = 0; i < rateLimit; i++) {
            mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk());
        }

        mockMvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isTooManyRequests());
    }
}

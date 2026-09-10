package me.ngarak.cita;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ShareLinksTest {

    @Test
    public void contentPlayUrl_hasUtm() {
        String url = ShareLinks.contentPlayUrl("wsc_test");
        assertTrue(url.contains("utm_source=tiktok"));
        assertTrue(url.contains("utm_campaign=wsc_test"));
        assertTrue(url.contains("id=me.ngarak.cita"));
    }

    @Test
    public void invitePlayUrl_hasReferrer() {
        String url = ShareLinks.invitePlayUrl("ABC123");
        assertTrue(url.contains("referrer="));
        assertTrue(url.contains("cita_invite") || url.contains("utm_source"));
    }
}

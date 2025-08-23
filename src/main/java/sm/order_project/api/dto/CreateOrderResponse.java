package sm.order_project.api.dto;

import lombok.Builder;
import lombok.Getter;
import sm.order_project.api.kakao.KakaoPayReadyResponse;

@Getter
public class CreateOrderResponse {

    private String tid; // 결제 고유 번호
    private String nextRedirectPcUrl; // 요청한 클라이언트가 PC 웹일 경우
    private String nextRedirectAppUrl;// 요청한 클라이언트가 모바일 앱일 경우
    private String nextRedirectMobileUrl;// 요청한 클라이언트가 모바일 웹일 경우
    private String androidAppScheme; //카카오페이 결제 화면으로 이동하는 Android 앱 스킴(Scheme)
    private String iosAppScheme; // 카카오페이 결제 화면으로 이동하는 iOS 앱 스킴
    private String createdAt;

    @Builder
    private CreateOrderResponse(String tid, String nextRedirectPcUrl, String nextRedirectAppUrl, String nextRedirectMobileUrl, String androidAppScheme, String iosAppScheme, String createdAt) {
        this.tid = tid;
        this.nextRedirectPcUrl = nextRedirectPcUrl;
        this.nextRedirectAppUrl = nextRedirectAppUrl;
        this.nextRedirectMobileUrl = nextRedirectMobileUrl;
        this.androidAppScheme = androidAppScheme;
        this.iosAppScheme = iosAppScheme;
        this.createdAt = createdAt;
    }

    public static CreateOrderResponse of(KakaoPayReadyResponse kakaoResp) {
        return CreateOrderResponse.builder()
                .tid(kakaoResp.getTid())
                .nextRedirectPcUrl(kakaoResp.getNextRedirectPcUrl())
                .nextRedirectAppUrl(kakaoResp.getNextRedirectAppUrl())
                .nextRedirectMobileUrl(kakaoResp.getNextRedirectMobileUrl())
                .androidAppScheme(kakaoResp.getAndroidAppScheme())
                .iosAppScheme(kakaoResp.getIosAppScheme())
                .createdAt(kakaoResp.getCreatedAt())
                .build();
    }

}

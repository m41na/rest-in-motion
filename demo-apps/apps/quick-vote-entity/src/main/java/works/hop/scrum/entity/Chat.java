package works.hop.scrum.entity;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {

    private String key;
    @NonNull
    private String scrumKey;
    @NonNull
    private String sender;
    @NonNull
    private String[] recipients;
    @NonNull
    private String subject;
    @NonNull
    private String message;
    @NonNull
    private Date dateSent;
}

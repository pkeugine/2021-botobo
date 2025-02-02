package botobo.core.dto.admin;

import botobo.core.domain.user.User;
import botobo.core.domain.workbook.Workbook;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class AdminWorkbookRequest {

    @NotBlank(message = "W002")
    @Length(max = 30, message = "W001")
    private String name;
    private boolean opened = true;

    public AdminWorkbookRequest(String name) {
        this.name = name;
    }

    public AdminWorkbookRequest(String name, boolean isPublic) {
        this.name = name;
        this.opened = isPublic;
    }

    public Workbook toWorkbook(User user) {
        return Workbook.builder()
                .name(name)
                .opened(true)
                .deleted(false)
                .opened(opened)
                .user(user)
                .build();
    }
}

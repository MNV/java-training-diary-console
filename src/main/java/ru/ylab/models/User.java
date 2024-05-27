package ru.ylab.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements BaseModel {
    private Long id;
    private String username;
    private String password;
    private boolean isAdmin;
}

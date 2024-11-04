package com.example.hope_dog.dto.donation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DonaCommentDTO {
    private Long donaCommentNo;
    private Long donaNo;
    private String donaCommentContent;
    private Date donaCommentRegidate;
    private Date donaCommentUpdatedate;
    private Long donaCommentWriter;
    private String commentWriterName;
}

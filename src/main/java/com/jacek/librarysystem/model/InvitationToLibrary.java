package com.jacek.librarysystem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name="invitation")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationToLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;





}

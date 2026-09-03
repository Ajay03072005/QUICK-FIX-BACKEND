package com.example.Quick_fix.Entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "PROVIDER")
@Data
public class ProviderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String uniqueId;

    private String name;

    private String phoneNumber;

    private String email;

    @OneToMany(mappedBy = "provider")
    private List<ProviderServiceHistoryEntity> providerServices;

    @OneToMany(mappedBy = "provider")
    private List<ProviderAddressEntity> addresses;

    @OneToMany(mappedBy = "provider")
    private List<ProviderDocumentEntity> documents;

    @OneToMany(mappedBy = "provider")
    private List<ProviderContactEntity> contacts;
}

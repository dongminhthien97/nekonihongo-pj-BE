package com.nekonihongo.backend.entity;

import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;

public @interface NamingStrategy {

    Class<PhysicalNamingStrategyStandardImpl> value();

}

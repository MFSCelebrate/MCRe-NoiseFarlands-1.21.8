/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.util.RealmsPersistence;

@Environment(value=EnvType.CLIENT)
public class RealmsNewsUpdater {
    final private RealmsPersistence persistence;
    private boolean hasUnreadNews;
    private String newsLink;

    public RealmsNewsUpdater(RealmsPersistence persistence) {
        this.persistence = persistence;
        RealmsPersistence.RealmsPersistenceData realmsPersistenceData = persistence.load();
        this.hasUnreadNews = realmsPersistenceData.hasUnreadNews;
        this.newsLink = realmsPersistenceData.newsLink;
    }

    public boolean hasUnreadNews() {
        return this.hasUnreadNews;
    }

    public String getNewsLink() {
        return this.newsLink;
    }

    public void updateNews(RealmsNews news) {
        RealmsPersistence.RealmsPersistenceData realmsPersistenceData = this.checkLinkUpdated(news);
        this.hasUnreadNews = realmsPersistenceData.hasUnreadNews;
        this.newsLink = realmsPersistenceData.newsLink;
    }

    private RealmsPersistence.RealmsPersistenceData checkLinkUpdated(RealmsNews news) {
        RealmsPersistence.RealmsPersistenceData realmsPersistenceData = this.persistence.load();
        if (news.newsLink == null || news.newsLink.equals(realmsPersistenceData.newsLink)) {
            return realmsPersistenceData;
        }
        RealmsPersistence.RealmsPersistenceData realmsPersistenceData2 = new RealmsPersistence.RealmsPersistenceData();
        realmsPersistenceData2.newsLink = news.newsLink;
        realmsPersistenceData2.hasUnreadNews = true;
        this.persistence.save(realmsPersistenceData2);
        return realmsPersistenceData2;
    }
}


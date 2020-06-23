package com.simple2young2.jpx.oodesign.command;

import java.time.LocalDateTime;

/**
 * 
 * @author JianpanxiaLiang
 *
 */
public class Backlog {
	
    private Integer id;

    private String title;
    
    private byte[] content;

    private Integer blockId;

    private LocalDateTime modificationTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Integer getBlockId() {
        return blockId;
    }

    public void setBlockId(Integer blockId) {
        this.blockId = blockId;
    }

    public LocalDateTime getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(LocalDateTime modificationTime) {
        this.modificationTime = modificationTime;
    }

}

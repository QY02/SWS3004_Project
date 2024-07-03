package org.cs304.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.cs304.backend.entity.Reply;
import org.cs304.backend.mapper.ReplyMapper;
import org.cs304.backend.service.IReplyService;
import org.springframework.stereotype.Service;

@Service
public class ReplyServiceImpl extends ServiceImpl<ReplyMapper, Reply> implements IReplyService {

}

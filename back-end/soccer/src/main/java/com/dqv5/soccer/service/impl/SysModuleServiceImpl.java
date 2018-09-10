package com.dqv5.soccer.service.impl;

import com.dqv5.soccer.dao.SysFuncRightMapper;
import com.dqv5.soccer.dao.SysModuleMapper;
import com.dqv5.soccer.entity.SysFuncRight;
import com.dqv5.soccer.entity.SysModule;
import com.dqv5.soccer.entity.TreeNode;
import com.dqv5.soccer.service.SysModuleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author duq
 * @date 2018/8/18
 */
@Service
public class SysModuleServiceImpl implements SysModuleService {

    @Resource
    private SysModuleMapper sysModuleMapper;
    @Resource
    private SysFuncRightMapper sysFuncRightMapper;

    @Override
    public List<SysModule> findList() {
        List<SysModule> list = sysModuleMapper.findList();
        return moduleListToTree(list);
    }

    @Override
    public List<SysModule> findListByUser(int userId) {
        List<SysModule> list = sysModuleMapper.findListByUser(userId);
        return moduleListToTree(list);
    }

    @Override
    public SysModule findOne(Integer id) {
        return sysModuleMapper.findOne(id);
    }

    @Override
    public SysModule save(SysModule sysModule) {
        if (sysModule.getId() == null) {
            sysModuleMapper.insert(sysModule);
        } else {
            sysModuleMapper.update(sysModule);
        }
        if (sysModule.getFuncRightList() != null) {
            sysFuncRightMapper.deleteByModule(sysModule.getId());
            for (SysFuncRight sysFuncRight : sysModule.getFuncRightList()) {
                sysFuncRightMapper.insert(sysFuncRight);
            }
        }
        return sysModule;
    }

    @Override
    public void delete(Integer id) {
        sysModuleMapper.delete(id);
    }


    private List<SysModule> moduleListToTree(List<SysModule> list) {
        List<SysModule> result = new ArrayList<>();
        for (SysModule sysModule : list) {
            if (sysModule.getParentId() == null) {
                pushChildren(sysModule, list);
                sysModule.setExpand("1");
                result.add(sysModule);
            }
        }
        return result;
    }


    private void pushChildren(SysModule module, List<SysModule> allList) {
        List<TreeNode> children = new ArrayList<>();
        for (SysModule sysModule : allList) {
            if (module.getId().equals(sysModule.getParentId())) {
                pushChildren(sysModule, allList);
                children.add(sysModule);
            }
        }
        module.setChildren(children);
    }
}

import {ref} from "vue";
export const EVENT_TYPE_MAP = {
    0: '讲座',
    1: '工作坊',
    2: '比赛',
    3: '表演',
    4: '展览',
    5: '论坛',
    6: '体育',
    7: '志愿',
    8: '学院',
    9: '沙龙',
    10: '培训',
    11: '社团',
    12: '其他',
};

export const EVENT_TYPE_value = ref([
    {label: '讲座', value: 1,},
    {label: '工作坊', value: 2,},
    {label: '比赛', value: 3},
    {label: '表演', value: 4},
    {label: '展览', value: 5},
    {label: '论坛', value: 6,},
    {label: '体育', value: 7},
    {label: '志愿', value: 8},
    {label: '学院', value: 9},
    {label: '沙龙', value: 10,},
    {label: '培训', value: 11},
    {label: '社团', value: 12},
    {label: '其他', value: 13},
]);
export const EVENT_TYPE_value_1 = ref([ //从0开始的
    {label: '讲座', value: 0,},
    {label: '工作坊', value: 1,},
    {label: '比赛', value: 2},
    {label: '表演', value: 3},
    {label: '展览', value: 4},
    {label: '论坛', value: 5,},
    {label: '体育', value: 6},
    {label: '志愿', value: 7},
    {label: '学院', value: 8},
    {label: '沙龙', value: 9,},
    {label: '培训', value: 10},
    {label: '社团', value: 11},
    {label: '其他', value: 12},
]);

// 通知主题映射
export const NOTIFICATION_THEMES = {
    0: 'default',
    1: 'success',
    2: 'danger',
    3: 'primary',
    4: 'warning',
    5: 'danger'
};
//通知类型映射
export const NOTIFICATION_TYPES = {
    0: '其他',
    1: '审核通过',
    2: '审核未通过',
    3: '成功参加活动',
    4: '活动通知',
    5: '活动取消'
};
// 定义 ADDITIONAL_INFO 数组
export const ADDITIONAL_INFO = [
    {
        label: "手机号",
        value: '{"name": "手机号", "nameEng": "phoneNumber", "required": true, "rules": [{"telnumber": true, "message": "请输入正确的手机号码"}], "value": ""}'
    },
    {
        label: "书院",
        value: '{"name": "书院","nameEng": "college", "required": true, "rules": [], "value": ""}',
    },
    {
        label: "身份证号码",
        value: '{"name": "身份证号码","nameEng": "IDNumber", "required": true, "rules": [{"idcard": true, "message": "请输入正确的身份证号码"}], "value": ""}'
    }
];

// 创建一个映射，将 value 映射到 label
export const ADDITIONAL_INFO_MAP = ADDITIONAL_INFO.reduce((map, item) => {
    map[item.value] = item.label;
    return map;
}, {});
//entity 映射
export const ENTITY_TYPE = {
    EVENT: 0,
    COMMENT: 1
};
export const DEPARTMENT_OPTIONS = ['计算机系','物理系','数学系'];
export const emailSuffix = ['@qq.com', '@163.com', '@gmail.com','@mail.sustech.edu.cn'];
export const avatarList = [
    'https://imgs.design006.com/Upload/test/Design006_20200905085032928.png',
    'https://avatars.githubusercontent.com/pengyyyyy',
    'https://tdesign.gtimg.com/site/avatar.jpg',
    'https://avatars.githubusercontent.com/LeeJim',
    'https://avatars.githubusercontent.com/u/7361184?v=4',
    'https://avatars.githubusercontent.com/pattybaby110',
    'https://avatars.githubusercontent.com/chaishi']



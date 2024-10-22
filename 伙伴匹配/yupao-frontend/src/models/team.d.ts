export  type TeamType = {
    id :number;
    teamName: string;
    description:string;
    maxNum:number;
    password?:string;
    userID:number;
    status:string;
    captainId:number;
    expireTime:Date;
};

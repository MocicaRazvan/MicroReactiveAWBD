import { AuthProvider, Role } from "./fetch-utils";

export interface IdDto {
  id: number;
}

export interface IdDtoTime extends IdDto {
  createdAt: string;
  updatedAt: string;
}

export interface WithUser extends IdDtoTime {
  userId: number;
}

export interface TitleBody {
  title: string;
  body: string;
  images: string[];
}

export interface TitleBodyUser extends TitleBody, WithUser {
  userLikes: number[];
  userDislikes: number[];
}

export interface Approve extends TitleBodyUser {
  approved: boolean;
}

export interface PageInfo {
  currentPage: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
}

export interface PageableResponse<T> {
  content: T;
  pageInfo: PageInfo;
  links: Record<string, string>[] | null;
}

export interface ResponseWithChildList<E, C> {
  entity: E;
  children: C[];
}

export interface CustomEntityModel<T> {
  content: T;
  _links?: Record<string, string>[] | null;
}

export interface ResponseWithChildListEntity<E, C> {
  entity: CustomEntityModel<E>;
  children: C[];
}

export interface UserDto extends IdDtoTime {
  firstName: string;
  lastName: string;
  email: string;
  role: Role;
  image: string;
  provider: AuthProvider;
  emailVerified: boolean;
}

export interface ResponseWithUserDto<T> {
  model: T;
  user: UserDto;
}

export interface ResponseWithUserDtoEntity<T> {
  model: CustomEntityModel<T>;
  user: UserDto;
}

export interface ResponseWithUserLikesAndDislikes<T>
  extends ResponseWithUserDto<T> {
  userLikes: UserDto[];
  userDislikes: UserDto[];
}

export interface ResponseWithUserLikesAndDislikesEntity<T>
  extends ResponseWithUserDtoEntity<T> {
  userLikes: UserDto[];
  userDislikes: UserDto[];
}

export interface PageableBody {
  page: number;
  size: number;
  sortingCriteria?: Record<string, string>;
}

export interface UserBody {
  firstName: string;
  lastName: string;
  image: string;
}

export interface CommentBody extends TitleBody {}

export interface CommentResponse extends TitleBodyUser {
  postId: number;
}

export interface ExerciseBody extends TitleBody {
  muscleGroups: string[];
  videos: string[];
}

export interface ExerciseBodyWithId extends ExerciseBody {
  id: number;
}

export interface ExerciseResponse extends Approve {
  muscleGroups: string[];
  videos: string[];
}

export interface ExerciseTrainingCount {
  count: number;
}

export interface OrderBody {
  shippingAddress: string;
  payed: boolean;
  trainings: number[];
}

export interface OrderStructure extends WithUser {
  shippingAddress: string;
  payed: boolean;
}

export interface OrderResponse extends OrderStructure {
  trainings: number[];
}

export interface PriceDto {
  price: number;
}

export interface PostBody extends TitleBody {
  tags: string[];
}

export interface PostResponse extends Approve {
  tags: string[];
}

export interface TrainingBody extends TitleBody {
  price: number;
  exercises: number[];
}

export interface TrainingResponse extends Approve {
  price: number;
  exercises: number[];
}

export interface ExerciseResponseWithTrainingCount extends ExerciseResponse {
  trainingCount: number;
  user: UserDto;
}

export interface TrainingResponseWithOrderCount extends TrainingResponse {
  orderCount: number;
  user: UserDto;
}

export type ConnectedStatus = "ONLINE" | "OFFLINE";

export interface ConversationUserBase {
  email: string;
  connectedStatus: ConnectedStatus;
}

export interface ConversationUserPayload extends ConversationUserBase {
  connectedChatRoomId?: number;
}

export interface ConnectUserPayload {
  connectedStatus: ConnectedStatus;
}

export interface ChatRoomPayload {
  users: ConversationUserBase[];
}

export interface ChatRoomResponse {
  id: number;
  users: ConversationUserResponse[];
}

export interface ConversationUserResponse extends ConversationUserBase {
  id: number;
  connectedChatRoom?: ChatRoomResponse;
}

export interface ChatMessagePayload {
  senderEmail: string;
  receiverEmail: string;
  chatRoomId: number;
  content: string;
}

export interface ChatMessageResponse {
  id: number;
  sender: ConversationUserResponse;
  receiver: ConversationUserResponse;
  chatRoom: ChatRoomResponse;
  content: string;
  timestamp: string;
}

export interface ChatRoomUserDto {
  chatId: number;
  userEmail: string;
}

export interface NotificationTemplateBody<E extends string> {
  senderEmail: string;
  receiverEmail: string;
  type?: E;
  referenceId: number;
  content: string;
  extraLink: string;
}

export type ChatMessageNotificationType = "NEW_MESSAGE";

export interface ChatMessageNotificationBody
  extends NotificationTemplateBody<ChatMessageNotificationType> {}

export interface NotificationTemplateResponse<R extends IdDto, E extends string>
  extends IdDto {
  sender: ConversationUserResponse;
  receiver: ConversationUserResponse;
  type: E;
  reference: R;
  content: string;
  extraLink: string;
  timestamp: string;
}

export interface ChatMessageNotificationResponse
  extends NotificationTemplateResponse<
    ChatRoomResponse,
    ChatMessageNotificationType
  > {}

export interface SenderTypeDto<E extends string> {
  senderEmail: string;
  type: E;
}

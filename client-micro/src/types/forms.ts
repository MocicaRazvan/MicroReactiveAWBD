import { Option } from "@/components/ui/multiple-selector";
import { z } from "zod";

export const updateProfileSchema = z.object({
  firstName: z.string().min(2, "First name must be at least 2 characters"),
  lastName: z.string().min(2, "Last name must be at least 2 characters"),
  image: z.array(z.string().url("Invalid url")).optional(),
});

export const emailSchema = z.object({
  email: z.string().email("Invalid email address"),
});

export const signInSchema = z
  .object({
    // email: z.string().email("Invalid email address"),
    password: z.string().min(4, "Password must be at least 4 characters"),
  })
  .and(emailSchema);

export const resetPasswordSchema = z
  .object({
    password: z.string().min(4, "Password must be at least 4 characters"),
    confirmPassword: z
      .string()
      .min(4, "Password must be at least 4 characters"),
  })
  .superRefine((data, ctx) => {
    if (data.password !== data.confirmPassword) {
      ctx.addIssue({
        path: ["confirmPassword"],
        message: "Passwords do not match",
        code: z.ZodIssueCode.custom,
      });
    }
  });

export const registerSchema = z
  .object({
    // firstName: z.string().min(2, "First name must be at least 2 characters"),
    // lastName: z.string().min(2, "Last name must be at least 2 characters"),
    confirmPassword: z
      .string()
      .min(4, "Password must be at least 4 characters"),
  })
  .and(signInSchema)
  .and(updateProfileSchema)
  .superRefine((data, ctx) => {
    if (data.password !== data.confirmPassword) {
      ctx.addIssue({
        path: ["confirmPassword"],
        message: "Passwords do not match",
        code: z.ZodIssueCode.custom,
      });
    }
  });

export const titleBodySchema = z.object({
  title: z.string().min(2, "Title must be at least 2 characters"),
  body: z.string().min(2, "Body must be at least 2 characters"),
});

export const commnetBodySchema = titleBodySchema;

export const ImageSchema = z.object({
  images: z
    .array(z.string().url("Invalid url"))
    .min(1, "Enter at least 1 image"),
});
export const postSchema = z
  .object({
    tags: z
      .array(z.object({ label: z.string(), value: z.string() }))
      .min(1, "Enter at least 1 tag"),
  })
  .and(titleBodySchema)
  .and(ImageSchema);

export const exerciseSchema = z
  .object({
    muscleGroups: z
      .array(z.object({ label: z.string(), value: z.string() }))
      .min(1, "Enter at least 1 tag"),
    videos: z
      .array(z.string().url("Invalid url"))
      .min(1, "Enter at least 1 video"),
  })
  .and(titleBodySchema)
  .and(ImageSchema);

export const trainingSchema = z
  .object({
    price: z.coerce.number().min(1, "Price must be at least 1"),
    exercises: z
      .array(z.object({ label: z.string(), value: z.string() }))
      .min(1, "Enter at least 1 exercise"),
  })
  .and(titleBodySchema)
  .and(ImageSchema);

export function createConfirmPriceSchema(currentTotalPrice: number) {
  return z.object({
    userConfirmedPrice: z.coerce
      .number()
      .refine(
        (value) => value === currentTotalPrice,
        `The confirmed amount must exactly match the total price of ${currentTotalPrice}.`,
      ),
  });
}

export function createCheckoutSchema(currentTotalPrice: number) {
  return z
    .object({
      billingAddress: z
        .string()
        .min(2, "Billing address must be at least 2 characters"),
    })
    .and(createConfirmPriceSchema(currentTotalPrice));
}

export const conversationMessageSchema = z.object({
  content: z.string().min(1, "Message must be at least 1 character"),
});

export type BasicFormProps = {
  submitText?: string;
  header?: string;
  path: string;
  method: "POST" | "PUT";
  callback?: () => void;
};
export type ResetPasswordType = z.infer<typeof resetPasswordSchema>;
export type EmailType = z.infer<typeof emailSchema>;
export type PostType = z.infer<typeof postSchema>;
export type UpdateProfileType = z.infer<typeof updateProfileSchema>;
export type SignInType = z.infer<typeof signInSchema>;
export type RegisterType = z.infer<typeof registerSchema>;
export type ExerciseType = z.infer<typeof exerciseSchema>;
export type CommentType = z.infer<typeof commnetBodySchema>;
export type TrainingType = z.infer<typeof trainingSchema>;
export type ConfirmPriceType = z.infer<
  ReturnType<typeof createConfirmPriceSchema>
>;
export type CheckoutType = z.infer<ReturnType<typeof createCheckoutSchema>>;
export type ConversationMessageType = z.infer<typeof conversationMessageSchema>;

export const tags = [
  "#wellness",
  "#fitness",
  "#nutrition",
  "#mentalhealth",
  "#yoga",
  "#meditation",
  "#mindfulness",
  "#selfcare",
] as const;

export const muscleGroups = [
  "Chest",
  "Back",
  "Shoulders",
  "Legs",
  "Arms",
  "Core",
  "Full Body",
  "Abs",
  "Glutes",
  "Biceps",
  "Triceps",
  "Hamstrings",
  "Quads",
  "Calves",
] as const;

export const tagsOptions: Option[] = tags.map((tag) => ({
  label: tag,
  value: tag,
}));

export const muscleGroupsOptions: Option[] = muscleGroups.map((group) => ({
  label: group,
  value: group,
}));

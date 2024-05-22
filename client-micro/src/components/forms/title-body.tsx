import {
  Control,
  UseFormRegister,
  FieldValues,
  ControllerRenderProps,
  Path,
} from "react-hook-form";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { TitleBody } from "@/types/dto";
import Editor from "../editor/editor";

interface CustomFieldProps<TFieldValues extends TitleBody> {
  control: Control<TFieldValues>;
  //   field: ControllerRenderProps<TFieldValues, Path<TFieldValues>>;
  titlePlaceholder?: string;
  bodyPlaceholder?: string;
}

export const TitleBodyForm = <TFieldValues extends TitleBody>({
  control,
  titlePlaceholder = "",
  bodyPlaceholder = "",
}: CustomFieldProps<TFieldValues>) => {
  return (
    <div className="space-y-4">
      <FormField
        control={control}
        name={"title" as Path<TFieldValues>}
        render={({ field }) => (
          <FormItem>
            <FormLabel>Title</FormLabel>
            <FormControl>
              <Input placeholder={titlePlaceholder} {...field} />
            </FormControl>
            <FormMessage />
          </FormItem>
        )}
      />
      <FormField
        control={control}
        name={"body" as Path<TFieldValues>}
        render={({ field }) => (
          <FormItem>
            <FormLabel>Body</FormLabel>
            <FormControl>
              {/* <Textarea placeholder={bodyPlaceholder} {...field} />
               */}
              <Editor
                descritpion={field.value as string}
                onChange={field.onChange}
                placeholder={bodyPlaceholder}
              />
            </FormControl>
            <FormMessage />
          </FormItem>
        )}
      />
    </div>
  );
};

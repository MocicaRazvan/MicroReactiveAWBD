"use client";
import {
  FieldValue,
  FieldValues,
  UseFormRegisterReturn,
  UseFormReturn,
  useFormContext,
} from "react-hook-form";
import { Button } from "../ui/button";
import { CldUploadButton, CldUploadWidget } from "next-cloudinary";
import { useRef, useState } from "react";

interface CloudinaryWidgetProps {
  formKey: string;
  type: "image" | "video";
  multiple: boolean;
  defaultValues?: string[];
}

export default function CustomCloudinaryWidget({
  formKey,
  type,
  multiple,
  defaultValues = [],
}: CloudinaryWidgetProps) {
  const form: UseFormReturn = useFormContext();
  const [items, setItems] = useState<string[]>(defaultValues);
  const plural = multiple ? type + "s" : type;
  const clientAllowedFormats =
    type === "image" ? ["jpg", "jpeg", "png"] : ["mp4", "mov", "avi"];

  return (
    <div
      className="z-[999999]"
      onClick={(e) => {
        e.stopPropagation();
      }}
    >
      <div className="flex gap-2 items-center justify-center ">
        <div>
          <CldUploadWidget
            uploadPreset="wellness_reactive"
            onSuccess={(results) => {
              if (typeof results.info === "object" && "url" in results.info) {
                form.setError(formKey, {});
                console.log(multiple);
                if (multiple) {
                  form.setValue(formKey, [
                    ...form.getValues()[formKey],
                    results.info.url,
                  ]);

                  setItems(form.getValues()[formKey]);
                } else {
                  form.setValue(formKey, [results.info.url]);
                  setItems([results.info.url]);
                }
                console.log(form.getValues()[formKey]);
              }
            }}
            options={{
              multiple,
              resourceType: type,
              clientAllowedFormats,
              sources: ["local"],
            }}
          >
            {({ open }) => (
              <Button type="button" onClick={() => open()}>
                Upload {plural}
              </Button>
            )}
          </CldUploadWidget>
        </div>
        {items.length > 0 && (
          <p>
            {" "}
            {`${items.length} ${items.length === 1 ? type : plural} uploaded`}
          </p>
        )}
      </div>
      {form.formState.errors[formKey]?.message && (
        <p className="text-destructive text-center mt-3 ">
          {form.formState.errors[formKey]?.message as string}
        </p>
      )}
      <div className="flex  items-center justify-center mt-10">
        {items.length > 0 && (
          <Button
            type="button"
            variant="destructive"
            onClick={() => {
              form.setValue(formKey, []);
              setItems([]);
            }}
          >
            Clear {plural}
          </Button>
        )}
      </div>
    </div>
  );
}
